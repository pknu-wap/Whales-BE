package com.whales.config;

import com.whales.user.application.TrustScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrustScoreScheduler {

    private final TrustScoreService trustScoreService;

    @Scheduled(cron = "0 0 3 * * *")  // 매일 새벽 3시
    public void updateScores() {
        trustScoreService.calculateAllUsersTrustScore();
    }
}
