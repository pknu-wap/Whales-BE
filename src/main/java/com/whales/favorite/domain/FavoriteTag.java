package com.whales.favorite.domain;

import com.whales.tag.domain.Tag;
import com.whales.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "favorite_tags",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "tag_id"}))
public class FavoriteTag {

    @EmbeddedId
    private Id id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    private Tag tag;

    private Instant createdAt = Instant.now();

    public FavoriteTag(User user, Tag tag) {
        this.id = new Id(user.getId(), tag.getId());
        this.user = user;
        this.tag = tag;
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    public static class Id {
        @Column(name = "user_id")
        private UUID userId;

        @Column(name = "tag_id")
        private UUID tagId;

        public Id(UUID userId, UUID tagId) {
            this.userId = userId;
            this.tagId = tagId;
        }
    }
}