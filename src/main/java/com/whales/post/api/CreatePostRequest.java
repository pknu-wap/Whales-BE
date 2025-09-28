package com.whales.post.api;

import jakarta.validation.constraints.NotBlank;

public record CreatePostRequest (
        @NotBlank String title,
        @NotBlank String content
){}
