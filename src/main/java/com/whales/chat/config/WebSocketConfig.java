package com.whales.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // 순수 WebSocket (테스트 용)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");

        // SockJS (웹 클라이언트 전용)
        registry.addEndpoint("/ws-sockjs")
                .setAllowedOriginPatterns("*")
                .withSockJS();

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트 구독 prefix
        config.enableSimpleBroker("/topic");
        // 서버로 메시지 보낼 때 prefix
        config.setApplicationDestinationPrefixes("/app");
    }
}
