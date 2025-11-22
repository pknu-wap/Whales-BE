package com.whales.notification.api;

import com.whales.notification.application.NotificationService;
import com.whales.notification.sse.SseEmitterManager;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

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

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id,
                                           @AuthenticationPrincipal WhalesUserPrincipal principal) {
        notificationService.markAsRead(id, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read/unread")
    public ResponseEntity<Void> markUnreadAsRead(@AuthenticationPrincipal WhalesUserPrincipal principal) {
        notificationService.markUnreadAsRead(principal.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public long getUnreadCount(@AuthenticationPrincipal WhalesUserPrincipal principal) {
        return notificationService.getUnreadCount(principal.getId());
    }

    @GetMapping("/unread")
    public List<NotificationResponse> getUnread(@AuthenticationPrincipal WhalesUserPrincipal principal) {
        return notificationService.getUnreadNotifications(principal.getId());
    }
}
