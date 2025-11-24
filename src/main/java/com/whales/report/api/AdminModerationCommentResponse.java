package com.whales.report.api;

import com.whales.comment.domain.Comment;

import java.time.Instant;
import java.util.UUID;

public record AdminModerationCommentResponse(
        UUID id,
        String body,
        String authorName,
        UUID postId,
        Instant createdAt
) {
    public static AdminModerationCommentResponse from(Comment c) {
        return new AdminModerationCommentResponse(
                c.getId(),
                c.getBody(),
                c.getAuthor().getDisplayName(),
                c.getPost().getId(),
                c.getCreatedAt()
        );
    }
}