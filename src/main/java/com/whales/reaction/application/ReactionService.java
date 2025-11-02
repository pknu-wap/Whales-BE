package com.whales.reaction.application;

import com.whales.reaction.api.ReactionSummary;
import com.whales.reaction.domain.Reaction;
import com.whales.reaction.domain.ReactionType;

import java.util.Optional;
import java.util.UUID;

public abstract class ReactionService<T extends Reaction> {

    protected abstract Optional<T> findReactionByUserAndTargetId(UUID userId, UUID targetId);
    protected abstract void saveReaction(T reaction);
    protected abstract void deleteReaction(T reaction);
    protected abstract ReactionSummary aggregateReactionSummary(UUID targetId, UUID userId);

    public ReactionSummary getReactionSummary(UUID targetId, UUID userId) {
        return aggregateReactionSummary(targetId, userId);
    }

    protected void toggleReaction(Optional<T> existing, T newReaction, ReactionType type) {
        existing.ifPresentOrElse(reaction -> {
            if (reaction.getType().equals(type)) {
                deleteReaction(reaction);
            } else {
                reaction.changeType(type);
            }
        }, () -> saveReaction(newReaction));
    }
}
