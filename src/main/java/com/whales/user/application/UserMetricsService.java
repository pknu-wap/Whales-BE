package com.whales.user.application;

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

    private UserMetrics load(UUID userId) {
        return userMetricsRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("UserMetrics not initialized for user: " + userId));
    }
}
