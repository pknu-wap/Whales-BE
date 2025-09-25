package com.whales.auth;

import com.whales.api.dto.request.GoogleLoginRequest;
import com.whales.api.dto.response.TokenResponse;
import com.whales.domain.user.User;
import com.whales.domain.user.UserRepository;
import com.whales.domain.user.UserRole;
import com.whales.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final GoogleOAuthService googleOAuthService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * 구글 OAuth2 로그인 (Access 토큰만 발급, Refresh는 아직 미적용)
     * 1) code → token → userinfo
     * 2) email 검증(@pukyong.ac.kr) + email_verified
     * 3) User upsert
     * 4) Access JWT 발급
     */
    @Transactional
    public TokenResponse loginWithGooogle(GoogleLoginRequest request) {
        // 구글 교환
        GoogleOAuthService.GoogleUser googleUser = googleOAuthService.exchange(request.code(), request.redirectUri());

        // 검증
        if (googleUser == null || googleUser.getEmail() == null || !googleUser.isEmailVerified()) {
            throw new IllegalArgumentException("Google email not verified");
        }
        String email = googleUser.getEmail().toLowerCase(Locale.ROOT);
        if (!email.endsWith("@pukyong.ac.kr")) {
            throw new IllegalArgumentException("Google email does not end with '@pukyong.ac.kr'");
        }

        // upsert
        Optional<User> foundUser = userRepository.findByEmail(email);
        User user;
        if (foundUser.isPresent()) {
            user = foundUser.get();
            String displayName = googleUser.getName() != null ? googleUser.getName() : user.getDisplayName();
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
            throw new EntityNotFoundException("Failed to persist user.");
        }

        String role = user.getRole().name().toLowerCase(Locale.ROOT);
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), role);

        long expiresInSeconds = 900L; // 동일 값으로 프론트와 약속
        return new TokenResponse(accessToken, expiresInSeconds);
    }
}
