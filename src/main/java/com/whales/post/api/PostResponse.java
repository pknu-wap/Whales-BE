package com.whales.post.api;

import java.time.Instant;
import java.util.UUID;


public record PostResponse (
        UUID id,
        String title,
        String content,
        String authorName,
        Instant createdAt,
        Instant updatedAt
){}
