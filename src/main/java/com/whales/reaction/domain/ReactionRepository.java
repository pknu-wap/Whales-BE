package com.whales.reaction.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, UUID> {

    /**
     * 특정 사용자가 특정 게시글에 남긴 반응을 찾습니다.
     * @param userId 사용자 ID
     * @param postId 게시글 ID
     * @return Reaction 객체 (존재하지 않으면 Optional.empty())
     */
    Optional<Reaction> findByUserIdAndPostId(UUID userId, UUID postId);

    /**
     * 특정 사용자가 특정 댓글에 남긴 반응을 찾습니다.
     * @param userId 사용자 ID
     * @param commentId 댓글 ID
     * @return Reaction 객체 (존재하지 않으면 Optional.empty())
     */
    Optional<Reaction> findByUserIdAndCommentId(UUID userId, UUID commentId);
}
