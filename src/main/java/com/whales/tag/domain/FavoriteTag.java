package com.whales.tag.domain;

import com.whales.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

// 즐겨찾기한 태그 엔티티
@Entity
@Table(name = "favorite_tags")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteTag {

    @EmbeddedId
    private Id id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Tag tag;

    private Instant createdAt = Instant.now();

    @Builder
    public FavoriteTag(User user, Tag tag) {
        this.id = new Id(user.getId(), tag.getId());
        this.user = user;
        this.tag = tag;
    }

    @Embeddable
    @Getter
    @Setter
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Id implements Serializable {
        @Column(nullable = false)
        private UUID userId;

        @Column(nullable = false)
        private UUID tagId;
    }
}
