package com.whales.report.api;

import com.whales.comment.domain.Comment;
import com.whales.post.domain.Post;
import com.whales.report.application.AdminModerationService;
import com.whales.user.domain.User;
import com.whales.user.domain.UserBadgeColor;
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
    @GetMapping("/users/{badgeColor}")
    public ResponseEntity<List<AdminModerationUserResponse>> getUsersByBadgeColor(@PathVariable UserBadgeColor badgeColor) {
        List<User> users = moderationService.findUsersByBadgeColor(badgeColor);
        return ResponseEntity.ok(
                users.stream().map(AdminModerationUserResponse::from).toList()
        );
    }
}