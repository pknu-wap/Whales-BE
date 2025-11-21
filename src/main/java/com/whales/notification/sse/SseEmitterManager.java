package com.whales.notification.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterManager {

    private final Map<UUID, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter add(UUID userId) {
        SseEmitter emitter = new SseEmitter(1000L * 60 * 10);

        emitters.computeIfAbsent(userId, k -> new ArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError((e) -> removeEmitter(userId, emitter));

        return emitter;
    }

    public void send(UUID userId, Object data) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters == null) return;

        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : userEmitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(data));
            } catch (Exception ex) {
                deadEmitters.add(emitter);
            }
        }

        userEmitters.removeAll(deadEmitters);
    }

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
    }
}