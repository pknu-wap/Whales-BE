package com.whales.tag.api;

import java.util.UUID;

public record TagResponse(
        UUID id,
        String name
) {
}
