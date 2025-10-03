package com.whales.auth.api;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest (
        @NotBlank String code,
        @NotBlank String redirectUri
) {}
