package com.whales.user.api;

import com.whales.user.domain.User;

import java.util.UUID;

public record UserSummary(
        UUID id,
        String displayName,
        String email,
        String nicknameColor
) {
    public static UserSummary from(User user) {
        if (user == null) return null;
        return new UserSummary(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getNicknameColor() != null ? user.getNicknameColor() : "GRAY"
        );
    }
}