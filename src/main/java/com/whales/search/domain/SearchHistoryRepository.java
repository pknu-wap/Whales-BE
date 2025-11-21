package com.whales.search.domain;

import com.whales.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, UUID> {

    List<SearchHistory> findByUserOrderBySearchedAtDesc(User user);

    Optional<SearchHistory> findByUserAndKeyword(User user, String keyword);
}
