package com.whales.comment.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommentRequest(
        @NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
        @Size(max = 5000, message = "댓글은 최대 5000자까지 입력할 수 있습니다.")
        String body
) {
}
