package com.whales.post.api;

import com.whales.post.domain.Post;
import com.whales.tag.api.TagResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public record PostResponse (
        UUID id,
        String title,
        String content,
        List<TagResponse> tags,
        String authorName,
        Instant createdAt,
        Instant updatedAt
){
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getPostTags().stream()
                        .map(pt -> new TagResponse(pt.getTag().getId(), pt.getTag().getName()))
                        .collect(Collectors.toList()),
                post.getAuthor().getDisplayName(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
