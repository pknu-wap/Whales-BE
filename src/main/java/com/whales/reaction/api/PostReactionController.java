package com.whales.reaction.api;

import com.whales.reaction.application.PostReactionService;
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
@RequestMapping("/posts")
@Tag(name = "Post Reaction API", description = "게시글 좋아요 / 싫어요 반응 관리 API")
public class PostReactionController {

    private final PostReactionService postReactionService;

    @Operation(
            summary = "게시글 좋아요 토글",
            description = """
                    사용자가 특정 게시글에 대해 좋아요를 토글합니다.
                    • 이미 좋아요였다면 → 좋아요 취소  
                    • 좋아요가 아니라면 → 좋아요 설정  
                    """
    )
    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> toggleLike(
            @Parameter(description = "좋아요를 토글할 게시글 ID")
            @PathVariable UUID postId,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        postReactionService.toggle(postId, principal.getId(), ReactionType.LIKE);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "게시글 싫어요 토글",
            description = """
                    사용자가 특정 게시글에 대해 싫어요를 토글합니다.
                    • 이미 싫어요였다면 → 싫어요 취소  
                    • 좋아요 혹은 무반응 상태라면 → 싫어요 설정  
                    """
    )
    @PostMapping("/{postId}/dislike")
    public ResponseEntity<Void> toggleDislike(
            @Parameter(description = "싫어요를 토글할 게시글 ID")
            @PathVariable UUID postId,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        postReactionService.toggle(postId, principal.getId(), ReactionType.DISLIKE);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "게시글의 좋아요/싫어요 현황 조회",
            description = """
                    특정 게시글의 좋아요 수 / 싫어요 수 및  
                    현재 사용자의 반응 상태(LIKE/DISLIKE/null)를 확인할 수 있습니다.
                    """
    )
    @GetMapping("/{postId}/reactions")
    public ResponseEntity<ReactionSummary> getPostReactionSummary(
            @Parameter(description = "반응 현황을 조회할 게시글 ID")
            @PathVariable UUID postId,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        ReactionSummary reactions = postReactionService.getReactionSummary(postId, principal.getId());
        return ResponseEntity.ok(reactions);
    }
}