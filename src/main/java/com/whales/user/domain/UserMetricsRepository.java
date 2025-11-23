package com.whales.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserMetricsRepository extends JpaRepository<UserMetrics, UUID> {
}
