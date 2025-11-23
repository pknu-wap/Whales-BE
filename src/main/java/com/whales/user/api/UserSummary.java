package com.whales.user.api;

import com.whales.user.domain.TrustLevel;
import com.whales.user.domain.User;
import com.whales.user.domain.UserBadgeColor;

import java.util.UUID;

public record UserSummary(
        UUID id,
        String displayName,
        String email,
        UserBadgeColor badgeColor,
        TrustLevel trustLevel
) {
    public static UserSummary from(User user) {
        if (user == null) return null;
        return new UserSummary(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getBadgeColor(),
                user.getTrustLevel()
        );
    }
}