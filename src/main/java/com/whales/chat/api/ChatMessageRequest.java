package com.whales.chat.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChatMessageRequest {
    @NotNull
    private UUID roomId;

    @NotBlank
    private String content;
}
