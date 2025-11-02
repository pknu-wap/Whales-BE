package com.whales.reaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PostReactionRepository extends JpaRepository<PostReaction, PostReaction.Id> {
    Optional<PostReaction> findByUser_IdAndPost_Id(UUID userId, UUID postId);
    @Query("""
        SELECT
            SUM(CASE WHEN r.type = 'LIKE' THEN 1 ELSE 0 END),
            SUM(CASE WHEN r.type = 'DISLIKE' THEN 1 ELSE 0 END),
            MAX(CASE WHEN r.user.id = :userId THEN r.type ELSE NULL END)
        FROM PostReaction r
        WHERE r.post.id = :postId
    """)
    Object[] getReactionSummary(@Param("postId") UUID postId, @Param("userId") UUID userId);}
