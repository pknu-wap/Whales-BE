package com.whales.notification.api;

import com.whales.notification.application.NotificationService;
import com.whales.notification.sse.SseEmitterManager;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final SseEmitterManager emitterManager;
    private final NotificationService notificationService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@AuthenticationPrincipal WhalesUserPrincipal principal) {
        return emitterManager.add(principal.getId());
    }

    @GetMapping
    public List<NotificationResponse> listMyNotifications(@AuthenticationPrincipal WhalesUserPrincipal principal) {
        return notificationService.getMyNotifications(principal.getId());
    }
}
