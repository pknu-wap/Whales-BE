package com.whales.admin.api;

import com.whales.admin.application.AdminModerationService;
import com.whales.comment.domain.Comment;
import com.whales.post.domain.Post;
import com.whales.user.domain.User;
import com.whales.user.domain.UserBadgeColor;
import com.whales.user.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/moderation")
@PreAuthorize("hasRole('ADMIN')")
public class AdminModerationController {

    private final AdminModerationService moderationService;

    /**
     * 차단된 게시글 목록
     */
    @GetMapping("/posts")
    public ResponseEntity<List<AdminModerationPostResponse>> getBlockedPosts() {
        List<Post> posts = moderationService.getBlockedPosts();
        return ResponseEntity.ok(
                posts.stream().map(AdminModerationPostResponse::from).toList()
        );
    }

    /**
     * 차단된 댓글 목록
     */
    @GetMapping("/comments")
    public ResponseEntity<List<AdminModerationCommentResponse>> getBlockedComments() {
        List<Comment> comments = moderationService.getBlockedComments();
        return ResponseEntity.ok(
                comments.stream().map(AdminModerationCommentResponse::from).toList()
        );
    }

    /**
     * BadgeColor별 유저 목록
     */
    @GetMapping("/users/badge/{badgeColor}")
    public ResponseEntity<List<AdminModerationUserResponse>> getUsersByBadgeColor(@PathVariable UserBadgeColor badgeColor) {
        List<User> users = moderationService.findUsersByBadgeColor(badgeColor);
        return ResponseEntity.ok(
                users.stream().map(AdminModerationUserResponse::from).toList()
        );
    }

    /**
     * Status별 유저 목록
     */
    @GetMapping("/users/status/{status}")
    public ResponseEntity<List<AdminModerationUserResponse>> getUsersByStatus(@PathVariable UserStatus status) {
        List<User> users = moderationService.findUsersByStatus(status);
        return ResponseEntity.ok(
                users.stream().map(AdminModerationUserResponse::from).toList()
        );
    }
}