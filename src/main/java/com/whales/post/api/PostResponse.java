package com.whales.post.api;

import com.whales.post.domain.Post;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PostResponse {
    private UUID id;
    private String title;
    private String content;
    private String authorName;
    private Instant createdAt;
    private Instant updatedAt;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorName(post.getAuthor().getDisplayName()) // User getDisplayName()
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
