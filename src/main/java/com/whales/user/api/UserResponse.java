package com.whales.user.api;

import com.whales.user.domain.TrustLevel;
import com.whales.user.domain.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String displayName,
        String nicknameColor,
        String status,
        String avatarUrl,
        TrustLevel trustLevel
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getDisplayName(),
                u.getNicknameColor(),
                u.getStatus().name(),
                u.getAvatarUrl(),
                u.getTrustLevel()
        );
    }
}
