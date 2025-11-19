package com.whales.auth.api;

import com.whales.auth.application.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${oauth2.google.redirect-uri}")
    private String googleRedirectUri;

    // 프론트 -> 서버
    @PostMapping("/login/google")
    public ResponseEntity<LoginResponse> loginGoogle(@Valid @RequestBody GoogleLoginRequest request,
                                                     HttpServletResponse response,
                                                     HttpServletRequest httpRequest) {

        String ua = httpRequest.getHeader("User-Agent");
        String ip = httpRequest.getRemoteAddr();

        LoginResponse token = authService.loginWithGoogle(
                new GoogleLoginRequest(
                        request.code(),
                        request.redirectUri(),
                        ua,
                        ip
                )
        );

        // RefreshToken을 HttpOnly 쿠키로 저장
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.refreshToken())
                .httpOnly(true)
                .secure(true) // HTTPS 환경에서만 동작
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(token);
    }

    // 구글 -> 서버 (테스트용)
//    @GetMapping("/login/google/callback")
//    public ResponseEntity<TokenResponse> loginGoogleCallback(@RequestParam("code") String code) {
//        TokenResponse token = authService.loginWithGoogle(new GoogleLoginRequest(code, googleRedirectUri));
//        return ResponseEntity.ok(token);
//    }
}