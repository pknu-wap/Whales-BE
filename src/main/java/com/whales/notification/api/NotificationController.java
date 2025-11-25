package com.whales.notification.api;

import com.whales.notification.application.NotificationService;
import com.whales.notification.sse.SseEmitterManager;
import com.whales.security.WhalesUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Notification API", description = "알림 조회 및 SSE 스트림 API")
public class NotificationController {

    private final SseEmitterManager emitterManager;
    private final NotificationService notificationService;

    @Operation(
            summary = "SSE 실시간 알림 스트림 연결",
            description = """
                    클라이언트와 SSE(Server-Sent Events) 스트림을 연결합니다.
                    새로운 알림이 발생하면 실시간(event-stream)으로 푸시됩니다.
                    """
    )
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        return emitterManager.add(principal.getId());
    }

    @Operation(
            summary = "내 알림 전체 조회",
            description = "로그인한 사용자의 모든 알림을 최신순으로 반환합니다."
    )
    @GetMapping
    public List<NotificationResponse> listMyNotifications(
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        return notificationService.getMyNotifications(principal.getId());
    }

    @Operation(
            summary = "특정 알림 읽음 처리",
            description = "알림 ID를 입력받아 읽음 상태로 변경합니다."
    )
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "읽음 처리할 알림 ID")
            @PathVariable UUID id,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        notificationService.markAsRead(id, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "모든 읽지 않은 알림 읽음 처리",
            description = "사용자의 모든 '읽지 않은(unread)' 알림을 일괄 읽음 처리합니다."
    )
    @PatchMapping("/read/unread")
    public ResponseEntity<Void> markUnreadAsRead(
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        notificationService.markUnreadAsRead(principal.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "읽지 않은 알림 개수 조회",
            description = "현재 로그인한 사용자의 읽지 않은 알림 개수를 반환합니다."
    )
    @GetMapping("/unread-count")
    public long getUnreadCount(
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        return notificationService.getUnreadCount(principal.getId());
    }

    @Operation(
            summary = "읽지 않은 알림 목록 조회",
            description = "아직 읽지 않은 알림만 필터링하여 반환합니다."
    )
    @GetMapping("/unread")
    public List<NotificationResponse> getUnread(
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        return notificationService.getUnreadNotifications(principal.getId());
    }
}