package com.whales.auth.application;

import com.whales.auth.api.GoogleLoginRequest;
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

    @Transactional
    public AuthResult loginWithGoogle(GoogleLoginRequest request) {

        // 1) 구글 인증
        GoogleOAuthService.GoogleUser googleUser =
                googleOAuthService.exchange(request.code(), request.redirectUri());

        if (googleUser == null || googleUser.getEmail() == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Failed to fetch Google userinfo");

        if (!googleUser.isEmailVerified())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Google email not verified");

        String email = googleUser.getEmail().toLowerCase(Locale.ROOT);
        if (!email.endsWith("@pukyong.ac.kr"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid email domain");

        // 2) 유저 찾기 또는 생성
        User user = userRepository.findByEmail(email)
                .map(u -> {
                    u.setDisplayName(googleUser.getName() != null ? googleUser.getName() : u.getDisplayName());
                    return userRepository.save(u);
                })
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmail(email);
                    u.setDisplayName(googleUser.getName() != null ? googleUser.getName() : "Whales User");
                    u.setRole(UserRole.USER);
                    return userRepository.save(u);
                });

        // 3) UserMetrics 생성
        userMetricsRepository.findById(user.getId()).orElseGet(() ->
                userMetricsRepository.save(new UserMetrics(user))
        );

        // 4) 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name().toLowerCase()
        );

        long expiresIn = accessExpirationMs / 1000L;

        String refreshToken = generateRefreshToken();

        // 동일 기기 로그인 시 기존 refreshSession 제거
        refreshSessionRepository.deleteByUser_IdAndUserAgentAndIp(
                user.getId(), request.userAgent(), request.ip()
        );

        // 새로운 세션 저장
        refreshSessionRepository.save(
                RefreshSession.builder()
                        .user(user)
                        .refreshToken(refreshToken)
                        .createdAt(Instant.now())
                        .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                        .userAgent(request.userAgent())
                        .ip(request.ip())
                        .build()
        );

        return new AuthResult(accessToken, refreshToken, expiresIn, user);
    }

    @Transactional
    public AuthResult refreshAccessToken(String refreshToken) {

        RefreshSession session = refreshSessionRepository
                .findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (session.isExpired()) {
            refreshSessionRepository.delete(session);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        User user = session.getUser();
        String newAccessToken = jwtUtil.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name().toLowerCase()
        );

        String newRefreshToken = generateRefreshToken();

        session.updateToken(newRefreshToken, Instant.now().plus(30, ChronoUnit.DAYS));
        refreshSessionRepository.save(session);

        long expiresIn = accessExpirationMs / 1000L;

        return new AuthResult(newAccessToken, newRefreshToken, expiresIn, user);
    }

    @Transactional
    public void logoutCurrentSession(String refreshToken) {
        refreshSessionRepository.findByRefreshToken(refreshToken)
                .ifPresent(refreshSessionRepository::delete);
    }

    @Transactional
    public void logoutAllSessions(UUID userId) {
        refreshSessionRepository.deleteByUser_Id(userId);
    }
}