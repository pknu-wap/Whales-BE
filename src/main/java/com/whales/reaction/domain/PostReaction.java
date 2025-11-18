package com.whales.reaction.domain;

import com.whales.post.domain.Post;
import com.whales.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "post_reactions")
@Getter
@NoArgsConstructor
public class PostReaction extends Reaction {

    @Embeddable
    @Getter
    @NoArgsConstructor
    public static class Id implements Serializable {
        private UUID userId;
        private UUID postId;

        public Id(UUID userId, UUID postId) {
            this.userId = userId;
        }
    }

    @EmbeddedId
    private Id id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User userRef; // Reaction.user와 중복되지만 @MapsId 때문에 필요

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    private Post post;

    public PostReaction(User user, Post post, ReactionType type) {
        super(user, type);
        this.id = new Id(user.getId(), post.getId());
        this.userRef = user;
        this.post = post;
    }
}
