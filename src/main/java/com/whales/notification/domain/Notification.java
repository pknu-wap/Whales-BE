package com.whales.notification.domain;

import com.whales.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @Column(name = "comment_id")
    private UUID commentId;

    @Column(name = "sender_name", nullable = false, length = 100)
    private String senderName;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(nullable = false)
    private boolean read;

    @Column(nullable = false)
    private Instant createdAt;

    public Notification(User receiver, UUID postId, UUID commentId, String senderName, String message) {
        this.receiver = receiver;
        this.postId = postId;
        this.commentId = commentId;
        this.senderName = senderName;
        this.message = message;
        this.read = false;
        this.createdAt = Instant.now();
    }
}
