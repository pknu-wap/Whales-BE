package com.whales.user.application;

import com.whales.user.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrustScoreService {

    private final UserRepository userRepository;
    private final UserMetricsRepository metricsRepository;

    @Transactional
    public void calculateAllUsersTrustScore() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            UserMetrics metrics = metricsRepository.findById(user.getId())
                    .orElse(null);
            if (metrics == null) continue;

            int score = calculateTrustScoreFor(metrics, user);

            user.setTrustScore(score);
            user.setTrustLevel(calculateLevel(score));
        }
    }

    private TrustLevel calculateLevel(int score) {
        if (score <= 30) return TrustLevel.ROOKIE;
        if (score <= 60) return TrustLevel.MEMBER;
        if (score <= 85) return TrustLevel.EXPERT;
        return TrustLevel.WHALE;
    }

    private int calculateTrustScoreFor(UserMetrics metrics, User user) {

        double activityScore = normalizeActivity(metrics);
        double contributionScore = normalizeContribution(metrics);
        double engagementScore = normalizeEngagement(metrics, user.getId());
        double stabilityScore = normalizeStability(user);

        double total =
                activityScore * 0.2 +
                        contributionScore * 0.4 +
                        engagementScore * 0.3 +
                        stabilityScore * 0.1;

        return (int) Math.round(total);
    }

    private double normalizeActivity(UserMetrics m) {
        int base = m.getPostsCount() * 1 + m.getCommentsCount() * 1;
        return Math.min(base, 100);
    }

    private double normalizeContribution(UserMetrics m) {
        int base = m.getLikesReceived() * 2 - m.getReportsCount() * 5;
        return Math.max(Math.min(base, 100), 0);
    }

    private double normalizeEngagement(UserMetrics m, UUID userId) {
        return Math.min(m.getCommentsCount() * 1.5, 100);
    }

    private double normalizeStability(User user) {
        long days = ChronoUnit.DAYS.between(
                user.getCreatedAt(),
                Instant.now()
        );
        return Math.min(days * 0.5, 100);
    }
}