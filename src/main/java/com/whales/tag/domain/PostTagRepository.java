package com.whales.tag.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostTagRepository extends JpaRepository<PostTag, PostTagId> {
    List<PostTag> findByPostId(UUID postId);
    void deleteByPostIdAndTagId(UUID postId, UUID tagId);
    void deleteAllByPostId(UUID postId);
    boolean existsByPostIdAndTagId(UUID postId, UUID tagId);
}
