package com.whales.reaction.api;

import com.whales.reaction.application.ReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// User ID는 Spring Security Context에서 가져온다고 가정합니다.
@RequiredArgsConstructor
@RestController
public class ReactionController {

    private final ReactionService reactionService;

    // TODO: @AuthenticationPrincipal 등을 사용하여 실제 userId를 가져오는 코드로 대체해야 합니다.
    private UUID getAuthenticatedUserId() {
        return UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef"); // 임시 사용자 ID
    }

    // ===================================
    // 1. 게시글 (Post) Reaction
    // ===================================

    /**
     * PUT /posts/{postId}/reactions : 게시글 좋아요/싫어요 설정 또는 타입 변경
     * @param postId 대상 게시글 ID
     * @param request ReactionType (LIKE 또는 DISLIKE)
     * @return 생성 또는 변경된 Reaction 정보
     */
    @PutMapping("/posts/{postId}/reactions")
    public ResponseEntity<ReactionResponse> setPostReaction(
            @PathVariable UUID postId,
            @Valid @RequestBody ReactionRequest request) {

        UUID userId = getAuthenticatedUserId();
        ReactionResponse response = reactionService.setPostReaction(userId, postId, request);

        // 200 OK: 리소스가 생성되거나 성공적으로 수정됨
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /posts/{postId}/reactions : 게시글 반응 삭제 (취소)
     * @param postId 대상 게시글 ID
     * @return 204 No Content
     */
    @DeleteMapping("/posts/{postId}/reactions")
    public ResponseEntity<Void> removePostReaction(@PathVariable UUID postId) {

        UUID userId = getAuthenticatedUserId();
        reactionService.removePostReaction(userId, postId);

        // 204 No Content: 성공적인 삭제
        return ResponseEntity.noContent().build();
    }


    // ===================================
    // 2. 댓글 (Comment) Reaction
    // ===================================

    /**
     * PUT /comments/{commentId}/reactions : 댓글 좋아요/싫어요 설정 또는 타입 변경
     * @param commentId 대상 댓글 ID
     * @param request ReactionType (LIKE 또는 DISLIKE)
     * @return 생성 또는 변경된 Reaction 정보
     */
    @PutMapping("/comments/{commentId}/reactions")
    public ResponseEntity<ReactionResponse> setCommentReaction(
            @PathVariable UUID commentId,
            @Valid @RequestBody ReactionRequest request) {

        UUID userId = getAuthenticatedUserId();
        ReactionResponse response = reactionService.setCommentReaction(userId, commentId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /comments/{commentId}/reactions : 댓글 반응 삭제 (취소)
     * @param commentId 대상 댓글 ID
     * @return 204 No Content
     */
    @DeleteMapping("/comments/{commentId}/reactions")
    public ResponseEntity<Void> removeCommentReaction(@PathVariable UUID commentId) {

        UUID userId = getAuthenticatedUserId();
        reactionService.removeCommentReaction(userId, commentId);

        return ResponseEntity.noContent().build();
    }
}