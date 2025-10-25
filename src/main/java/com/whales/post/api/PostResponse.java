package com.whales.post.api;

import com.whales.post.domain.Post;

import java.time.Instant;
import java.util.UUID;


public record PostResponse (
        UUID id,
        String title,
        String content,
        String authorName,
        Instant createdAt,
        Instant updatedAt
){
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getDisplayName(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
