package com.whales.report.api;

import com.whales.post.domain.Post;

import java.time.Instant;
import java.util.UUID;

public record AdminModerationPostResponse(
        UUID id,
        String title,
        String authorName,
        Instant createdAt
) {
    public static AdminModerationPostResponse from(Post p) {
        return new AdminModerationPostResponse(
                p.getId(),
                p.getTitle(),
                p.getAuthor().getDisplayName(),
                p.getCreatedAt()
        );
    }
}