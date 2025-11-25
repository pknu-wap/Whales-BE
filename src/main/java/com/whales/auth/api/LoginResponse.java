package com.whales.auth.api;

import com.whales.user.api.UserResponse;
import com.whales.user.domain.User;

public record LoginResponse(
        String accessToken,
        long expiresIn,
        UserResponse user
) {
    public static LoginResponse from(String accessToken, Long expiresIn, User user) {
        return new LoginResponse(accessToken, expiresIn, UserResponse.from(user));
    }
}
