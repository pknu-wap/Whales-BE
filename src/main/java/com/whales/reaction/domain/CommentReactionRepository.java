package com.whales.reaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, CommentReaction.Id> {
    Optional<CommentReaction> findByUser_IdAndComment_Id(UUID userId, UUID commentId);
    @Query("""
    SELECT 
        SUM(CASE WHEN r.type = 'LIKE' THEN 1 ELSE 0 END),
        SUM(CASE WHEN r.type = 'DISLIKE' THEN 1 ELSE 0 END),
        MAX(CASE WHEN r.user.id = :userId THEN r.type ELSE NULL END)
    FROM CommentReaction r
    WHERE r.comment.id = :commentId
""")
    List<Object[]> getReactionSummary(@Param("commentId") UUID commentId, @Param("userId") UUID userId);
}
