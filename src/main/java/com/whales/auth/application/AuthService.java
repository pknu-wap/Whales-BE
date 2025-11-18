package com.whales.auth.application;

import com.whales.auth.api.GoogleLoginRequest;
import com.whales.auth.api.TokenResponse;
import com.whales.auth.infra.GoogleOAuthService;
import com.whales.security.JwtUtil;
import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import com.whales.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final GoogleOAuthService googleOAuthService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.access.expiration}")
    private long accessExpirationMs;

    /**
     * 구글 OAuth2 로그인
     * 1) code → token → userinfo
     * 2) email 검증(@pukyong.ac.kr) + email_verified
     * 3) User upsert
     * 4) Access JWT 발급
     */
    @Transactional
    public TokenResponse loginWithGoogle(GoogleLoginRequest request) {
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

        // 4) JWT 발급
        String role = user.getRole().name().toLowerCase(Locale.ROOT);
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), role);
        long expiresInSeconds = Math.floorDiv(accessExpirationMs, 1000L);

        // ✅ TokenResponse.from() 사용하여 MeResponse 변환
        return TokenResponse.from(accessToken, expiresInSeconds, user);
    }
}