package com.whales.auth.api;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest (
        @NotBlank String code,
        @NotBlank String redirectUri,
        // 클라이언트 환경 식별값
        String userAgent,
        String ip
) {}
