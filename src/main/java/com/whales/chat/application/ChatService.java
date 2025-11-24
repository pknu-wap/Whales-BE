package com.whales.chat.application;

import com.whales.chat.api.ChatMessageResponse;
import com.whales.chat.domain.ChatMessage;
import com.whales.chat.domain.ChatMessageRepository;
import com.whales.chat.domain.ChatRoom;
import com.whales.chat.domain.ChatRoomRepository;
import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository roomRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatRoom getOrCreateRoomByTag(String tagName) {
        return roomRepository.findByTagNameIgnoreCase(tagName)
                .orElseGet(() -> roomRepository.save(new ChatRoom(tagName)));
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(UUID roomId) {
        return messageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    @Transactional
    public void handleIncomingMessage(UUID roomId, String content, UUID senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ChatRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        ChatMessage msg = new ChatMessage();
        msg.setRoom(room);
        msg.setSender(sender);
        msg.setContent(content);
        msg.setCreatedAt(Instant.now());

        ChatMessage saved = messageRepository.save(msg);

        ChatMessageResponse resp = new ChatMessageResponse(
                saved.getId(),
                room.getId(),
                sender.getId(),
                sender.getDisplayName(),
                saved.getContent(),
                saved.getCreatedAt()
        );

        // 브로드캐스트: /topic/chat/{roomId}
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, resp);
    }

    @Transactional
    public void sendRestMessage(UUID roomId, UUID senderId, String content) {
        // REST에서 메시지를 보낼 때도 동일 로직을 호출
        handleIncomingMessage(roomId, content, senderId);
    }
}
