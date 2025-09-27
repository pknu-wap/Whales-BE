package com.whales.api.dto.response;

import java.util.UUID;

public record MeResponse(
        UUID id,
        String email,
        String displayName,
        String nicknameColor,
        String status,
        String avatarUrl
) {}
