package com.whales.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    // UUID로 게시물을 찾되 deletedAt이 NULL인 (삭제되지 않은) 게시물만 찾습니다.
    Optional<Post> findByIdAndDeletedAtIsNull(UUID id);

    // 모든 게시물을 찾되, deletedAt이 NULL인 (삭제되지 않은) 게시물만 찾습니다.
    // List<Post> findAllByDeletedAtIsNull();
}
