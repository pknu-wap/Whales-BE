package com.whales.reaction.api;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * 좋아요/싫어요 API 응답 DTO
 */
@Builder
public record ReactionResponse(
        UUID id,
        String type,
        String message,
        ZonedDateTime createdAt
) { }
