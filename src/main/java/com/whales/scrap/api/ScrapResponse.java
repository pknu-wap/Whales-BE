package com.whales.scrap.api;

import lombok.Builder;
import java.util.UUID;

/**
 * 스크랩 API 응답 DTO (record 버전)
 */
@Builder
public record ScrapResponse(
        UUID userId,
        UUID targetId,
        String targetType,
        boolean isScrapped
) {
    public static ScrapResponse of(UUID userId, UUID postId, UUID commentId, boolean isScrapped) {
        UUID targetId = postId != null ? postId : commentId;
        String targetType = postId != null ? "POST" : "COMMENT";
        return ScrapResponse.builder()
                .userId(userId)
                .targetId(targetId)
                .targetType(targetType)
                .isScrapped(isScrapped)
                .build();
    }
}
