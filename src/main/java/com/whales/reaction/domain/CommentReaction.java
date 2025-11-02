package com.whales.reaction.domain;

import com.whales.comment.domain.Comment;
import com.whales.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "comment_reactions")
@Getter
@NoArgsConstructor
public class CommentReaction extends Reaction {

    @Embeddable
    @Getter
    @NoArgsConstructor
    public static class Id implements Serializable {
        private UUID userId;
        private UUID commentId;

        public Id(UUID userId, UUID commentId) {
            this.userId = userId;
            this.commentId = commentId;
        }
    }

    @EmbeddedId
    private Id id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User userRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentId")
    private Comment comment;

    public CommentReaction(User user, Comment comment, ReactionType type) {
        super(user, type);
        this.id = new Id(user.getId(), comment.getId());
        this.userRef = user;
        this.comment = comment;
    }
}
