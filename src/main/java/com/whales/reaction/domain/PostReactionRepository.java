package com.whales.reaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PostReactionRepository extends JpaRepository<PostReaction, PostReaction.Id> {
    Optional<PostReaction> findByUserIdAndPostId(UUID userId, UUID postId);
    long countByPostIdAndType(UUID postId, ReactionType type);
    void deleteByUserIdAndPostId(UUID userId, UUID postId);
}
