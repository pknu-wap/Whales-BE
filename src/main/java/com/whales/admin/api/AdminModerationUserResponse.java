package com.whales.admin.api;

import com.whales.user.domain.User;
import com.whales.user.domain.UserBadgeColor;

import java.util.UUID;

public record AdminModerationUserResponse(
        UUID id,
        String email,
        String displayName,
        UserBadgeColor badgeColor
) {
    public static AdminModerationUserResponse from(User u) {
        return new AdminModerationUserResponse(
                u.getId(),
                u.getEmail(),
                u.getDisplayName(),
                u.getBadgeColor()
        );
    }
}