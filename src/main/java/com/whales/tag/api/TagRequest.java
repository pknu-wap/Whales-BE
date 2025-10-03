package com.whales.tag.api;

import jakarta.validation.constraints.NotBlank;

public record TagRequest(
        @NotBlank(message = "태그 이름은 비워둘 수 없습니다.")
        String name
) {
    public TagRequest {
        if (name != null) {
            // 공백 정리
            name = name.trim();
        }
    }
}
