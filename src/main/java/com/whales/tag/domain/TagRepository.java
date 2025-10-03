package com.whales.tag.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    Optional<Tag> findByNameIgnoreCase(String name);

    // 자동완성: 특정 prefix 로 시작하는 태그 검색
    List<Tag> findByNameStartingWithIgnoreCase(String prefix);
}
