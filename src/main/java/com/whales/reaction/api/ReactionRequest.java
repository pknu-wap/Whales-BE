package com.whales.reaction.api;

import com.whales.reaction.domain.Reaction;
import jakarta.validation.constraints.NotNull;

/**
 * 좋아요/싫어요 API 요청 DTO (PUT 요청 시 사용)
 */
public record ReactionRequest(
        @NotNull(message = "Reaction type must not be null.")
        Reaction.ReactionType type
) { }
