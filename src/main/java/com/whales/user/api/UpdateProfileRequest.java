package com.whales.user.api;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 1, max = 40)
        String displayName,

        @Size(max = 2048)
        String avatarUrl
) {}
