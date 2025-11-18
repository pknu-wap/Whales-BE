package com.whales.tag.api;

import com.whales.tag.domain.Tag;

import java.util.UUID;

public record TagResponse(
        UUID id,
        String name
) {
    public static TagResponse from(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }
}
