package com.whales.post.api;

import java.util.List;

public record UpdatePostRequest(
        String title,
        String content,
        List<String> tags
) {}
