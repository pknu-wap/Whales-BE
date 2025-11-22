package com.whales.notification.application;

import com.whales.comment.domain.Comment;
import com.whales.notification.api.NotificationResponse;
import com.whales.notification.domain.Notification;
import com.whales.notification.domain.NotificationRepository;
import com.whales.notification.sse.SseEmitterManager;
import com.whales.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
