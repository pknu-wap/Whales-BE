package com.whales.post.api;

public record UpdatePostRequest(
        String title,
        String content
) {}
