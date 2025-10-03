package com.whales.user.api;

import com.whales.user.domain.User;

import java.util.UUID;

public record MeResponse(
        UUID id,
        String email,
        String displayName,
        String nicknameColor,
        String status,
        String avatarUrl
) {
    public static MeResponse from(User u) {
        return new MeResponse(
                u.getId(),
                u.getEmail(),
                u.getDisplayName(),
                u.getNicknameColor(),
                u.getStatus().name(),
                u.getAvatarUrl()
        );
    }
}
