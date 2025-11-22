package com.whales.notification.application;

import com.whales.comment.domain.Comment;
import com.whales.notification.api.NotificationResponse;
import com.whales.notification.domain.Notification;
import com.whales.notification.domain.NotificationRepository;
import com.whales.notification.sse.SseEmitterManager;
import com.whales.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitterManager emitterManager;

    public void notifyNewComment(Post post, Comment comment) {
        // 1) DB 저장
        Notification notification = new Notification(
                post.getAuthor(),
                post.getId(),
                comment.getId(),
                comment.getAuthor().getDisplayName(),
                "새로운 댓글이 달렸습니다."
        );
        notificationRepository.save(notification);

        // 2) SSE 실시간 전송
        emitterManager.send(post.getAuthor().getId(), NotificationResponse.from(notification));
    }

    public List<NotificationResponse> getMyNotifications(UUID userId) {
        return notificationRepository.findByReceiver_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        if (!notification.getReceiver().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void markUnreadAsRead(UUID userId) {
        notificationRepository.markUnreadAsRead(userId);
    }

    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByReceiver_IdAndReadFalse(userId);
    }

    public List<NotificationResponse> getUnreadNotifications(UUID userId) {
        return notificationRepository
                .findByReceiver_IdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }
}
