package com.whales.reaction.api;

import com.whales.reaction.domain.ReactionType;

public record ReactionSummary(
        long likeCount,
        long dislikeCount,
        ReactionType myReaction
) {
}
