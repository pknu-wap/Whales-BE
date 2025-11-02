package com.whales.reaction.domain;

import com.whales.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

// DB에 직접 매핑하지 않고 자식 엔티티에서 공통 필드를 상속
@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class Reaction {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    protected User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    protected ReactionType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    protected Instant createdAt = Instant.now();

    public Reaction(User user, ReactionType type) {
        this.user = user;
        this.type = type;
    }

    public void changeType(ReactionType newType) {
        this.type = newType;
    }

    public UUID getUserId() {
        return user.getId();
    }
}
