package com.whales.scrap.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostScrapRepository extends JpaRepository<PostScrap, PostScrap.Id> {

    Optional<PostScrap> findByUser_IdAndPost_Id(UUID userId, UUID postId);
    List<PostScrap> findByUser_Id(UUID userId);
    boolean existsByUser_IdAndPost_Id(UUID userId, UUID postId);
}