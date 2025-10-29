package com.whales.post.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    // UUID로 게시물을 찾되 deletedAt이 NULL인 (삭제되지 않은) 게시물만 찾습니다.
    Optional<Post> findByIdAndDeletedAtIsNull(UUID id);

    // 모든 게시물을 찾되, deletedAt이 NULL인 (삭제되지 않은) 게시물만 찾습니다.
    //List<Post> findAllByDeletedAtIsNull();

    @Query("""
        SELECT p FROM Post p
        JOIN p.postTags pt
        JOIN pt.tag t
        WHERE LOWER(t.name) IN :names
        GROUP BY p
        HAVING COUNT(DISTINCT t.id) = :tagCount
        """)
    List<Post> findPostsByAllTagNames(@Param("names") List<String> names, @Param("tagCount") Integer tagCount);

    // LazyLoading(n+1 문제) 방지
    @Query("""
        SELECT DISTINCT p FROM Post p
        LEFT JOIN FETCH p.postTags pt
        LEFT JOIN FETCH pt.tag
        LEFT JOIN FETCH p.author
        WHERE p.id = :id
        """)
    Optional<Post> findByIdWithTagsAndAuthor(@Param("id") UUID id);
}