package com.whales.comment.api;

import com.whales.comment.domain.Comment;
import com.whales.common.ContentStatus;
import com.whales.reaction.api.ReactionSummary;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID postId,
        UUID authorId,
        String content,
        ContentStatus status,
        Instant createdAt,
        Instant updatedAt,
        ReactionSummary reactions
) {
    public static CommentResponse from(Comment comment, ReactionSummary reactions) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getAuthor().getId(),
                comment.getBody(),
                comment.getStatus(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                reactions
        );
    }
}
