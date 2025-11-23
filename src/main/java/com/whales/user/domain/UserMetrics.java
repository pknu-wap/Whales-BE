package com.whales.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_metrics")
public class UserMetrics {

    @Id
    @Column(name = "userId")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;

    private int postsCount;        // 작성한 게시글 수
    private int commentsCount;     // 작성한 댓글 수
    private int likesReceived;     // 받은 좋아요 수
    private int likesGiven;        // 남긴 좋아요 수
    private int reportsCount;      // 신고 횟수
    private Instant lastActiveAt;  // 마지막 활동 시각

    private Instant updatedAt;

    public UserMetrics(User user) {
        this.user = user;
        this.userId = user.getId();
        this.postsCount = 0;
        this.commentsCount = 0;
        this.likesReceived = 0;
        this.likesGiven = 0;
        this.reportsCount = 0;
        this.lastActiveAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void touch() {
        this.lastActiveAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void increasePosts() { postsCount++; touch(); }
    public void increaseComments() { commentsCount++; touch(); }
    public void increaseLikesReceived() { likesReceived++; touch(); }
    public void increaseLikesGiven() { likesGiven++; touch(); }
    public void increaseReports() { reportsCount++; touch(); }
}
