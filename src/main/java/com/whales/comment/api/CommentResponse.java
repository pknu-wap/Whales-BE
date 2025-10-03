package com.whales.comment.api;

import com.whales.comment.domain.Comment;
import com.whales.common.ContentStatus;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID postId,
        UUID authorId,
        String content,
        ContentStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getAuthor().getId(),
                comment.getBody(),
                comment.getStatus(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
