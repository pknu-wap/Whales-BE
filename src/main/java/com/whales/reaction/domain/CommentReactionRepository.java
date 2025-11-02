package com.whales.reaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, CommentReaction.Id> {
    Optional<CommentReaction> findByUser_IdAndComment_Id(UUID userId, UUID commentId);
    long countByComment_IdAndType(UUID commentId, ReactionType type);
}
