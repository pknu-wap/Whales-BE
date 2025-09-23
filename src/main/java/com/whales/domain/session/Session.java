package com.whales.domain.session;

import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter;
import java.time.Instant; import java.util.UUID;

@Entity @Table(name="sessions")
@Getter @Setter
public class Session {
    @Id @GeneratedValue
    private UUID id;

    @Column(name="user_id", nullable=false)
    private UUID userId;

    @Column(name="refresh_hash", nullable=false, columnDefinition="TEXT")
    private String refreshHash;

    @Column(name="user_agent", columnDefinition="TEXT")
    private String userAgent;

    @Column(name="ip_address")
    private String ipAddress; // Postgres INET ↔ String 매핑

    @Column(name="expires_at", nullable=false)
    private Instant expiresAt;

    @Column(name="created_at", nullable=false)
    private Instant createdAt = Instant.now();

    @Column(name="revoked_at")
    private Instant revokedAt;
}