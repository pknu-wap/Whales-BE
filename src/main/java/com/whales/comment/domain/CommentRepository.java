package com.whales.comment.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    // 삭제되지 않은(soft delete X) + 활성 상태의 댓글만 조회
    List<Comment> findByPost_IdAndDeletedAtIsNullAndStatusOrderByCreatedAtDesc(UUID postId, Enum<?> status);

    Optional<Comment> findByIdAndDeletedAtIsNull(UUID id);

    List<Comment> findByAuthor_IdAndDeletedAtIsNullAndStatusOrderByCreatedAtDesc(UUID userId, Enum<?> status);

    boolean existsByIdAndAuthor_Id(UUID id, UUID authorId);
}
