package com.whales.auth.application;

import com.whales.auth.api.GoogleLoginRequest;
import com.whales.auth.api.LoginResponse;
import com.whales.auth.domain.RefreshSession;
import com.whales.auth.domain.RefreshSessionRepository;
import com.whales.auth.infra.GoogleOAuthService;
import com.whales.security.JwtUtil;
import com.whales.user.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final GoogleOAuthService googleOAuthService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshSessionRepository refreshSessionRepository;
    private final UserMetricsRepository userMetricsRepository;

    @Value("${jwt.access.expiration}")
    private long accessExpirationMs;

    private String generateRefreshToken() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }

    /**
     * 구글 OAuth2 로그인
     * 1) code → token → userinfo
     * 2) email 검증(@pukyong.ac.kr) + email_verified
     * 3) User upsert
     * 4) JWT 발급
     */
    @Transactional
    public LoginResponse loginWithGoogle(GoogleLoginRequest request) {
        if (request == null || request.code() == null || request.redirectUri() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code/redirectUri is required");
        }

        // 1) 구글 교환
        GoogleOAuthService.GoogleUser googleUser = googleOAuthService.exchange(request.code(), request.redirectUri());

        // 2) 검증
        if (googleUser == null || googleUser.getEmail() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Failed to fetch Google userinfo");
        }
        if (!googleUser.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google email not verified");
        }

        String email = googleUser.getEmail().toLowerCase(Locale.ROOT);
        if (!email.endsWith("@pukyong.ac.kr")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Google email does not end with '@pukyong.ac.kr'");
        }

        // 3) upsert
        Optional<User> foundUser = userRepository.findByEmail(email);
        User user;
        if (foundUser.isPresent()) {
            user = foundUser.get();
            String displayName = googleUser.getName() != null
                    ? googleUser.getName()
                    : user.getDisplayName();
            user.setDisplayName(displayName);
            user = userRepository.save(user);
        } else {
            user = new User();
            user.setEmail(email);
            user.setDisplayName(googleUser.getName() != null ? googleUser.getName() : "Whales User");
            user.setRole(UserRole.USER);
            user = userRepository.save(user);
        }

        if (user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to persist user");
        }

        // userMetrics 생성
        UserMetrics metrics = userMetricsRepository.findById(user.getId()).orElse(null);
        if (metrics == null) {
            userMetricsRepository.save(new UserMetrics(user));
        }

        // 4) JWT 발급
        String role = user.getRole().name().toLowerCase(Locale.ROOT);
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), role);
        long expiresInSeconds = Math.floorDiv(accessExpirationMs, 1000L);

        String refreshToken = generateRefreshToken();

        // 같은 user + userAgent + ip 조합으로 기존 세션 제거 (중복 방지)
        refreshSessionRepository.deleteByUser_IdAndUserAgentAndIp(
                user.getId(),
                request.userAgent(),
                request.ip()
        );
        RefreshSession session = RefreshSession.builder()
                .user(user)
                .refreshToken(refreshToken)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                .userAgent(request.userAgent())
                .ip(request.ip())
                .build();

        refreshSessionRepository.save(session);

        return LoginResponse.from(accessToken, refreshToken, expiresInSeconds, user);
    }

    public LoginResponse refreshAccessToken(String refreshToken) {

        RefreshSession session = refreshSessionRepository
                .findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (session.isExpired()) {
            refreshSessionRepository.delete(session);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        User user = session.getUser();
        String role = user.getRole().name().toLowerCase(Locale.ROOT);

        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), role);

        String newRefreshToken = generateRefreshToken();
        session.updateToken(newRefreshToken, Instant.now().plus(30, ChronoUnit.DAYS));
        refreshSessionRepository.save(session);

        long expiresInSeconds = Math.floorDiv(accessExpirationMs, 1000L);

        return LoginResponse.from(newAccessToken, newRefreshToken, expiresInSeconds, user);
    }

    // 현재 기기에서만 로그아웃 (해당 RefreshToken 세션만 삭제)
    @Transactional
    public void logoutCurrentSession(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is required");
        }

        Optional<RefreshSession> sessionOpt = refreshSessionRepository.findByRefreshToken(refreshToken);
        if (sessionOpt.isEmpty()) {
            // 이미 삭제됐거나 잘못된 토큰일 수 있으니 404보단 400/401 정도 선택
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh session not found");
        }

        refreshSessionRepository.delete(sessionOpt.get());
    }

    // 해당 유저의 모든 세션 로그아웃 (모든 기기에서 로그아웃)
    @Transactional
    public void logoutAllSessions(UUID userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id is required");
        }

        refreshSessionRepository.deleteByUser_Id(userId);
    }
}