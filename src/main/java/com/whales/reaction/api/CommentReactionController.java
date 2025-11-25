package com.whales.reaction.api;

import com.whales.reaction.application.CommentReactionService;
import com.whales.reaction.domain.ReactionType;
import com.whales.security.WhalesUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Tag(name = "Comment Reaction API", description = "댓글 좋아요 / 싫어요 반응 관리 API")
public class CommentReactionController {

    private final CommentReactionService commentReactionService;

    @Operation(
            summary = "댓글 좋아요 토글",
            description = """
                    사용자가 특정 댓글에 대해 좋아요를 토글합니다.
                    이미 좋아요 상태였다면 좋아요가 취소되며,
                    좋아요가 아닌 상태라면 좋아요로 설정됩니다.
                    """
    )
    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> toggleLike(
            @Parameter(description = "좋아요 토글할 댓글 ID")
            @PathVariable UUID commentId,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        commentReactionService.toggle(commentId, principal.getId(), ReactionType.LIKE);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "댓글 싫어요 토글",
            description = """
                    사용자가 특정 댓글에 대해 싫어요를 토글합니다.
                    이미 싫어요 상태였다면 취소되고,
                    좋아요 혹은 아무 반응 없는 상태라면 싫어요로 설정됩니다.
                    """
    )
    @PostMapping("/{commentId}/dislike")
    public ResponseEntity<Void> toggleDislike(
            @Parameter(description = "싫어요 토글할 댓글 ID")
            @PathVariable UUID commentId,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        commentReactionService.toggle(commentId, principal.getId(), ReactionType.DISLIKE);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "댓글 반응(좋아요/싫어요) 조회",
            description = """
                    특정 댓글에 대한 좋아요/싫어요 개수 및
                    현재 사용자의 반응 상태(LIKE / DISLIKE / null)를 반환합니다.
                    """
    )
    @GetMapping("/{commentId}/reactions")
    public ResponseEntity<ReactionSummary> getCommentReactionSummary(
            @Parameter(description = "조회할 댓글 ID")
            @PathVariable UUID commentId,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        ReactionSummary reactions = commentReactionService.getReactionSummary(commentId, principal.getId());
        return ResponseEntity.ok(reactions);
    }
}