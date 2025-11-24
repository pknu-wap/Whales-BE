package com.whales.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        }
)
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    // email: CITEXT + @pukyong.ac.kr 체크 (DB 제약조건에서 보장)
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "display_name", length = 40, nullable = false)
    private String displayName;

    @Column(name = "last_display_name_change")
    private Instant lastDisplayNameChange;

    @Column(name = "bio", length = 200)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_color", nullable = false, length = 16)
    private UserBadgeColor badgeColor = UserBadgeColor.WHITE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=16)
    private UserRole role = UserRole.USER;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "trust_score", nullable = false)
    private int trustScore = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrustLevel trustLevel = TrustLevel.ROOKIE;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        if (this.lastDisplayNameChange == null) {
            this.lastDisplayNameChange = Instant.now();
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}