package com.whales.auth.api;

public record TokenResponse(
        String accessToken,
        long expiresIn
) {
}
