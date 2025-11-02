package com.whales.reaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, CommentReaction.Id> {
    Optional<CommentReaction> findByUserIdAndCommentId(UUID userId, UUID commentId);
    long countByCommentIdAndType(UUID commentId, ReactionType type);
    void deleteByUserIdAndCommentId(UUID userId, UUID commentId);
}
