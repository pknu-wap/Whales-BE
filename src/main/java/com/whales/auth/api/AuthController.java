package com.whales.auth.api;

import com.whales.auth.application.AuthResult;
import com.whales.auth.application.AuthService;
import com.whales.security.WhalesUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "로그인, 토큰 재발급, 로그아웃 등 인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "구글 OAuth2 로그인",
            description = "구글 인증 코드를 전달받아 액세스 토큰 및 리프레시 토큰을 발급합니다."
    )
    @PostMapping("/login/google")
    public ResponseEntity<LoginResponse> loginGoogle(
            @Valid @RequestBody GoogleLoginRequest request,
            HttpServletResponse response,
            HttpServletRequest httpRequest
    ) {

        String ua = httpRequest.getHeader("User-Agent");
        String ip = httpRequest.getRemoteAddr();

        AuthResult result = authService.loginWithGoogle(
                new GoogleLoginRequest(request.code(), request.redirectUri(), ua, ip)
        );

        // RefreshToken 쿠키 저장
        ResponseCookie cookie = ResponseCookie.from("refreshToken", result.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(
                LoginResponse.from(result.accessToken(), result.expiresIn(), result.user())
        );
    }

    @Operation(
            summary = "AccessToken 재발급",
            description = "HttpOnly 쿠키로 저장된 RefreshToken을 이용해 새 AccessToken을 발급합니다."
    )
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {

        if (refreshToken == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing refresh token");

        AuthResult result = authService.refreshAccessToken(refreshToken);

        // refresh 토큰 재발급
        ResponseCookie cookie = ResponseCookie.from("refreshToken", result.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(
                LoginResponse.from(result.accessToken(), result.expiresIn(), result.user())
        );
    }

    @Operation(
            summary = "로그아웃",
            description = "현재 기기 또는 모든 기기 세션을 종료하고 RefreshToken 쿠키를 삭제합니다."
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal WhalesUserPrincipal principal,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {

        if (principal == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");

        if (refreshToken != null)
            authService.logoutCurrentSession(refreshToken);
        else
            authService.logoutAllSessions(principal.getId());

        // 쿠키 제거
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.noContent().build();
    }
}