package com.whales.scrap.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, UUID> {

    Optional<Scrap> findByUserIdAndPostId(UUID userId, UUID postId);

    Optional<Scrap> findByUserIdAndCommentId(UUID userId, UUID commentId);
}
