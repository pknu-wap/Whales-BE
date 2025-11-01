package com.whales.reaction.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * 좋아요/싫어요 반응 엔티티 (REACTIONS 테이블)
 *
 * 제약 조건:
 * 1. post_id와 comment_id 중 하나만 NULL이 아니어야 함 (one_target_check_constraint)
 * 2. user_id와 post_id 또는 user_id와 comment_id 쌍은 유니크해야 함 (중복 방지)
 */
@Entity
@Table(name = "reactions", uniqueConstraints = {
        // Post에 대한 반응은 user_id와 post_id 쌍으로 유니크해야 함 (DB partial index로 구현 권장)
        @UniqueConstraint(columnNames = {"user_id", "post_id"}, name = "uq_reactions_user_post"),
        // Comment에 대한 반응은 user_id와 comment_id 쌍으로 유니크해야 함 (DB partial index로 구현 권장)
        @UniqueConstraint(columnNames = {"user_id", "comment_id"}, name = "uq_reactions_user_comment")
})
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Reaction {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    // 게시글 ID (댓글 ID와 상호 배타적)
    @Column(name = "post_id", columnDefinition = "uuid")
    private UUID postId;

    // 댓글 ID (게시글 ID와 상호 배타적)
    @Column(name = "comment_id", columnDefinition = "uuid")
    private UUID commentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType type;

    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp with time zone")
    private ZonedDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }

    /**
     * Reaction 타입 정의 (좋아요/싫어요)
     */
    public enum ReactionType {
        LIKE, DISLIKE
    }

    // =============================
    // 비즈니스 로직
    // =============================

    /**
     * Reaction 타입을 변경합니다. (PUT 요청 대응)
     * @param newType 새로 설정할 ReactionType
     */
    public void changeReactionType(ReactionType newType) {
        if (this.type != newType) {
            this.type = newType;
        }
    }
}