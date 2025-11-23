package com.whales.notification.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseEmitterManager {

    // userId -> emitter list
    private final Map<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    // SSE 연결 생성 및 등록
    public SseEmitter add(UUID userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.computeIfAbsent(userId, k -> new ArrayList<>()).add(emitter);

        log.info("[SSE] connected user={}", userId);

        // 연결 종료/타임아웃/에러 발생 시 emitter 제거
        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError((e) -> removeEmitter(userId, emitter));

        // 연결 직후 connect 이벤트 전송 (프론트에서 연결 확인 가능)
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (Exception e) {
            removeEmitter(userId, emitter);
        }
        return emitter;
    }

    // 특정 유저에게 SSE 이벤트 전송 (댓글 생성)
    public void send(UUID userId, Object data) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null) return;

        List<SseEmitter> deadEmitters = new ArrayList<>();

        // 복사본 순회 → ConcurrentModification 방지
        for (SseEmitter emitter : new ArrayList<>(userEmitters)) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(data));
            } catch (Exception ex) {
                deadEmitters.add(emitter);
            }
        }

        // 끊긴 emitter 정리
        userEmitters.removeAll(deadEmitters);

        if (userEmitters.isEmpty()) {
            emitters.remove(userId);
        }
    }

    // 모든 유저에게 heartbeat 전송 -> Proxy 및 Gateway에서 idle disconnect 방지
    public void sendHeartbeat() {
        for (UUID userId : emitters.keySet()) {
            send(userId, "keep-alive");
        }
    }

    private void removeEmitter(UUID userId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(userId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) {
                emitters.remove(userId);
            }
        }

        log.info("[SSE] disconnected user={}, remaining={}",
                userId, emitters.getOrDefault(userId, Collections.emptyList()).size());
    }
}