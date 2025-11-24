package com.whales.user.api;

import com.whales.user.domain.TrustLevel;
import com.whales.user.domain.User;
import com.whales.user.domain.UserBadgeColor;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String displayName,
        String bio,
        UserBadgeColor badgeColor,
        String status,
        String avatarUrl,
        TrustLevel trustLevel
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getDisplayName(),
                u.getBio(),
                u.getBadgeColor(),
                u.getStatus().name(),
                u.getAvatarUrl(),
                u.getTrustLevel()
        );
    }
}
