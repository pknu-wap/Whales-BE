package com.whales.auth.api;

import com.whales.user.api.MeResponse;
import com.whales.user.domain.User;
import lombok.Builder;

@Builder
public record TokenResponse(
        String accessToken,
        long expiresIn,
        MeResponse user
) {
    public static TokenResponse from(String accessToken, long expiresIn, User user) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .user(MeResponse.from(user))
                .build();
    }
}