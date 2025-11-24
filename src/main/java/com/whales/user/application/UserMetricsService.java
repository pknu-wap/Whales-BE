package com.whales.user.application;

import com.whales.user.domain.User;
import com.whales.user.domain.UserBadgeColor;
import com.whales.user.domain.UserMetrics;
import com.whales.user.domain.UserMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserMetricsService {

    private final UserMetricsRepository userMetricsRepository;

    @Transactional
    public void increasePostCount(UUID userId) {
        UserMetrics metrics = load(userId);
        metrics.increasePosts();
    }

    @Transactional
    public void increaseCommentCount(UUID userId) {
        UserMetrics metrics = load(userId);
        metrics.increaseComments();
    }

    @Transactional
    public void increaseLikesReceived(UUID userId) {
        UserMetrics metrics = load(userId);
        metrics.increaseLikesReceived();
    }

    @Transactional
    public void increaseLikesGiven(UUID userId) {
        UserMetrics metrics = load(userId);
        metrics.increaseLikesGiven();
    }

    @Transactional
    public void increaseReportsCount(UUID userId) {
        UserMetrics metrics = load(userId);
        metrics.increaseReports();

        User user = metrics.getUser();

        if (metrics.getReportsCount() >= 5) {
            user.setBadgeColor(UserBadgeColor.RED);
        } else if (metrics.getReportsCount() >= 3) {
            user.setBadgeColor(UserBadgeColor.ORANGE);
        }
    }

    private UserMetrics load(UUID userId) {
        return userMetricsRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("UserMetrics not initialized for user: " + userId));
    }
}
