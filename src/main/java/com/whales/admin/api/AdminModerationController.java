package com.whales.admin.api;

import com.whales.admin.application.AdminModerationService;
import com.whales.comment.domain.Comment;
import com.whales.post.domain.Post;
import com.whales.user.domain.User;
import com.whales.user.domain.UserBadgeColor;
import com.whales.user.domain.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin Moderation API", description = "관리자용 차단 콘텐츠 및 위험 유저 관리 API")
public class AdminModerationController {

    private final AdminModerationService moderationService;

    @Operation(summary = "차단된 게시글 목록 조회")
    @GetMapping("/posts")
    public ResponseEntity<List<AdminModerationPostResponse>> getBlockedPosts() {
        List<Post> posts = moderationService.getBlockedPosts();
        return ResponseEntity.ok(
                posts.stream().map(AdminModerationPostResponse::from).toList()
        );
    }

    @Operation(summary = "차단된 댓글 목록 조회")
    @GetMapping("/comments")
    public ResponseEntity<List<AdminModerationCommentResponse>> getBlockedComments() {
        List<Comment> comments = moderationService.getBlockedComments();
        return ResponseEntity.ok(
                comments.stream().map(AdminModerationCommentResponse::from).toList()
        );
    }

    @Operation(summary = "BadgeColor 기준 위험 유저 조회")
    @GetMapping("/users/badge/{badgeColor}")
    public ResponseEntity<List<AdminModerationUserResponse>> getUsersByBadgeColor(@PathVariable UserBadgeColor badgeColor) {
        List<User> users = moderationService.findUsersByBadgeColor(badgeColor);
        return ResponseEntity.ok(
                users.stream().map(AdminModerationUserResponse::from).toList()
        );
    }

    @Operation(summary = "Status 기준 유저 조회")
    @GetMapping("/users/status/{status}")
    public ResponseEntity<List<AdminModerationUserResponse>> getUsersByStatus(@PathVariable UserStatus status) {
        List<User> users = moderationService.findUsersByStatus(status);
        return ResponseEntity.ok(
                users.stream().map(AdminModerationUserResponse::from).toList()
        );
    }
}