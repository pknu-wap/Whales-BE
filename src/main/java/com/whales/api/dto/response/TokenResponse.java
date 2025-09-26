package com.whales.api.dto.response;

public record TokenResponse(
        String accessToken,
        long expiresIn
) {
}
