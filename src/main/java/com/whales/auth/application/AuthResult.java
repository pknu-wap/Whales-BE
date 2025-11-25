package com.whales.auth.application;

import com.whales.user.domain.User;

public record AuthResult(
        String accessToken,
        String refreshToken,
        long expiresIn,
        User user
) {}