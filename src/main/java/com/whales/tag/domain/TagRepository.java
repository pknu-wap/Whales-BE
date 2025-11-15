package com.whales.tag.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    Optional<Tag> findByNameIgnoreCase(String name);

    // prefix 로 시작하는 태그를 usage count 기준으로 정렬(자동완성)
    @Query("""
        SELECT t
        FROM Tag t
        LEFT JOIN t.postTags pt
        WHERE LOWER(t.name) LIKE LOWER(CONCAT(:prefix, '%'))
        GROUP BY t
        ORDER BY COUNT(pt) DESC, t.name ASC
        """)
    List<Tag> findPopularTagsByPrefix(@Param("prefix") String prefix, Pageable pageable);

    // 내가 즐겨찾기한 태그 목록 조회
    @Query("""
        SELECT ft.tag
        FROM FavoriteTag ft
        WHERE ft.user.id = :userId
        ORDER BY ft.tag.createdAt ASC
        """)
    List<Tag> findFavoriteTagsByUserId(UUID userId);

}
