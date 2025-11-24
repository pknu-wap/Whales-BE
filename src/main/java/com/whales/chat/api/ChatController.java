package com.whales.chat.api;

import com.whales.chat.application.ChatService;
import com.whales.chat.domain.ChatMessage;
import com.whales.chat.domain.ChatRoom;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponse> createRoom(@RequestParam String tagName) {
        ChatRoom room = chatService.getOrCreateRoomByTag(tagName);
        return ResponseEntity.ok(new ChatRoomResponse(room.getId(), room.getTagName()));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable UUID roomId) {
        List<ChatMessage> msgs = chatService.getMessages(roomId);
        List<ChatMessageResponse> res = msgs.stream().map(m ->
                new ChatMessageResponse(m.getId(), m.getRoom().getId(), m.getSender().getId(),
                        m.getSender().getDisplayName(), m.getContent(), m.getCreatedAt())
        ).collect(Collectors.toList());

        return ResponseEntity.ok(res);
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Void> postMessageRest(@PathVariable UUID roomId,
                                                @RequestBody String content,
                                                @AuthenticationPrincipal WhalesUserPrincipal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        chatService.sendRestMessage(roomId, principal.getId(), content);
        return ResponseEntity.ok().build();
    }
}
