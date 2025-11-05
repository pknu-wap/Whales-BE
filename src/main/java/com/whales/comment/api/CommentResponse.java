package com.whales.comment.api;

import com.whales.comment.domain.Comment;
import com.whales.common.ContentStatus;
import com.whales.reaction.api.ReactionSummary;
import com.whales.user.api.UserSummary;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID postId,
        String content,
        ContentStatus status,
        Instant createdAt,
        Instant updatedAt,
        ReactionSummary reactions,
        UserSummary author
) {
    public static CommentResponse from(Comment comment, ReactionSummary reactions) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getBody(),
                comment.getStatus(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                reactions,
                UserSummary.from(comment.getAuthor())
        );
    }
}
