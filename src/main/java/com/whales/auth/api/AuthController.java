package com.whales.auth.api;

import com.whales.auth.application.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${oauth2.google.redirect-uri}")
    private String googleRedirectUri;

    // 프론트 -> 서버
    @PostMapping("/login/google")
    public ResponseEntity<TokenResponse> loginGoogle(@Valid @RequestBody GoogleLoginRequest request) {

        TokenResponse token = authService.loginWithGoogle(request);
        return ResponseEntity.ok(token);
    }

    // 구글 -> 서버 (테스트용)
    @GetMapping("/login/google/callback")
    public ResponseEntity<TokenResponse> loginGoogleCallback(@RequestParam("code") String code) {
        TokenResponse token = authService.loginWithGoogle(new GoogleLoginRequest(code, googleRedirectUri));
        return ResponseEntity.ok(token);
    }
}