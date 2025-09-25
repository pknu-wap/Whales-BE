package com.whales.api;

import com.whales.api.dto.request.GoogleLoginRequest;
import com.whales.api.dto.response.TokenResponse;
import com.whales.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/google")
    public ResponseEntity<TokenResponse> loginGoogle(@Valid @RequestBody GoogleLoginRequest request) {

        TokenResponse token = authService.loginWithGooogle(request);
        return ResponseEntity.ok(token);
    }

}