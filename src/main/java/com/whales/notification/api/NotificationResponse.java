package com.whales.notification.api;

import com.whales.notification.domain.Notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String message,
        boolean read,
        Instant createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
