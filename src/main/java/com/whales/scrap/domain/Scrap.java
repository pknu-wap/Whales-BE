package com.whales.scrap.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "scraps", uniqueConstraints = {
        @UniqueConstraint(name = "uq_scraps_user_post", columnNames = {"user_id", "post_id"}),
        @UniqueConstraint(name = "uq_scraps_user_comment", columnNames = {"user_id", "comment_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Scrap {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "post_id")
    private UUID postId;

    @Column(name = "comment_id")
    private UUID commentId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Check(constraints = "(post_id IS NULL AND comment_id IS NOT NULL) OR (post_id IS NOT NULL AND comment_id IS NULL)")
    @Column(insertable = false, updatable = false)
    private String scrapOneTargetCheck;

    @Builder
    public Scrap(UUID userId, UUID postId, UUID commentId) {
        if ((postId == null) == (commentId == null)) {
            throw new IllegalArgumentException("Post ID or Comment ID must be provided, but not both.");
        }
        this.userId = userId;
        this.postId = postId;
        this.commentId = commentId;
    }
}
