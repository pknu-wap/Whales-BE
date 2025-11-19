package com.whales.auth.domain;

import com.whales.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token", nullable = false, updatable = true, length = 512)
    private String refreshToken;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    // 어떤 기기/브라우저에서 로그인했는지
    @Column(name = "user_agent", nullable = false, length = 255)
    private String userAgent;

    // 로그인 당시 IP
    @Column(name = "ip", nullable = false, length = 45)
    private String ip;

    @Builder
    public RefreshSession(
            User user,
            String refreshToken,
            Instant createdAt,
            Instant expiresAt,
            String userAgent,
            String ip
    ) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.userAgent = userAgent;
        this.ip = ip;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public void updateToken(String newToken, Instant newExpiresAt) {
        this.refreshToken = newToken;
        this.expiresAt = newExpiresAt;
    }
}
