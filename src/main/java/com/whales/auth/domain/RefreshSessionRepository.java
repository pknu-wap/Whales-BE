package com.whales.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, UUID> {
    Optional<RefreshSession> findByRefreshToken(String refreshToken);

    void deleteByUser_Id(UUID userId);

    void deleteByUser_IdAndUserAgentAndIp(UUID userId, String userAgent, String ip);
}
