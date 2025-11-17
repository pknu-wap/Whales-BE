package com.whales.favorite.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FavoriteTagRepository extends JpaRepository<FavoriteTag, FavoriteTag.Id> {
    boolean existsByUser_IdAndTag_Id(UUID userId, UUID tagId);

    void deleteByUser_IdAndTag_Id(UUID userId, UUID tagId);

    @Query("""
    SELECT ft
    FROM FavoriteTag ft
    JOIN FETCH ft.tag
    WHERE ft.user.id = :userId
    ORDER BY ft.createdAt ASC
    """)
    List<FavoriteTag> findByUser_Id(UUID userId);
}
