package com.whales.tag.api;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record TagListRequest(
        @NotEmpty(message = "하나 이상의 태그가 필요합니다.")
        List<@jakarta.validation.constraints.NotBlank String> tags
) {
}
