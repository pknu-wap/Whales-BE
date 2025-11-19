package com.whales.auth.api;

import com.whales.user.api.MeResponse;
import com.whales.user.domain.User;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        MeResponse user
) {
    public static LoginResponse from(String accessToken, String refreshToken, Long expiresIn, User user) {
        return new LoginResponse(accessToken, refreshToken, expiresIn, MeResponse.from(user));
    }
}
