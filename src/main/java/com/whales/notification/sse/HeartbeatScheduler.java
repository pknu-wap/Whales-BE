package com.whales.notification.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HeartbeatScheduler {

    private final SseEmitterManager emitterManager;

    @Scheduled(fixedRate = 30000) // 30초마다 실행
    public void sendHeartbeat() {
        emitterManager.sendHeartbeat();
    }
}
