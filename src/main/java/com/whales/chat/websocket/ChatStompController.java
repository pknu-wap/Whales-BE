package com.whales.chat.websocket;

import com.whales.chat.application.ChatService;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final ChatService chatService;

    // 클라이언트에서 /app/chat.send 로 메시지 전송
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageStompPayload payload,
                            @AuthenticationPrincipal WhalesUserPrincipal principal) {

        // ChatService에서 저장 & 브로드캐스트 처리
        chatService.handleIncomingMessage(payload.roomId(), payload.content(), principal.getId());

    }

    // 메시지 payload 클래스는 아래에 정의하거나 별도 파일로
    public static record ChatMessageStompPayload(UUID roomId, String content) {}
}
