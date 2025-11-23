package com.whales.auth.api;

import com.whales.user.api.UserResponse;
import com.whales.user.domain.User;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        UserResponse user
) {
    public static LoginResponse from(String accessToken, String refreshToken, Long expiresIn, User user) {
        return new LoginResponse(accessToken, refreshToken, expiresIn, UserResponse.from(user));
    }
}
