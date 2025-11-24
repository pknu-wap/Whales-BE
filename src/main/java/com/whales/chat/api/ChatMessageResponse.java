package com.whales.chat.api;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageResponse(
        UUID id,
        UUID roomId,
        UUID senderId,
        String senderName,
        String content,
        Instant createdAt
) {
}
