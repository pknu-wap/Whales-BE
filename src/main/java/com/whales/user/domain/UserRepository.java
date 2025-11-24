package com.whales.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    long countByBadgeColor(UserBadgeColor color);
    List<User> findByBadgeColor(UserBadgeColor badgeColor);

    List<User> findUserByStatus(UserStatus status);

    Long countByStatus(UserStatus status);
}
