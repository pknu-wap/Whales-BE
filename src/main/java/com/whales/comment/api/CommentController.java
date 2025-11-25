package com.whales.comment.api;

import com.whales.comment.application.CommentService;
import com.whales.security.WhalesUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment API", description = "댓글 조회/작성/수정/삭제 API")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "게시글 댓글 목록 조회",
            description = "게시글 ID를 전달받아 해당 게시글의 전체 댓글 목록을 반환합니다. 로그인한 사용자는 자신의 반응 정보를 포함해 반환됩니다."
    )
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> listCommentByPost(
            @Parameter(description = "댓글을 조회할 게시글 ID") @PathVariable("postId") UUID postId,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        UUID userId = (principal != null) ? principal.getId() : null;
        List<CommentResponse> list = commentService.listByPost(postId, userId);
        return ResponseEntity.ok(list);
    }

    @Operation(
            summary = "특정 댓글 상세 조회",
            description = "댓글 ID를 통해 댓글 상세 내용과 로그인 유저의 반응(LIKE/DISLIKE) 정보를 반환합니다."
    )
    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> getCommentById(
            @Parameter(description = "조회할 댓글 ID") @PathVariable("id") UUID id,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        UUID userId = (principal != null) ? principal.getId() : null;
        CommentResponse comment = commentService.getById(id, userId);
        return ResponseEntity.ok(comment);
    }

    @Operation(
            summary = "내가 작성한 댓글 목록 조회",
            description = "현재 로그인된 사용자가 작성한 모든 댓글을 조회합니다."
    )
    @GetMapping("/comments/me")
    public ResponseEntity<List<CommentResponse>> listCommentByUser(
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        List<CommentResponse> list = commentService.listByUserId(principal.getId());
        return ResponseEntity.ok(list);
    }

    @Operation(
            summary = "댓글 작성",
            description = "해당 게시글에 새로운 댓글을 작성합니다."
    )
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @Parameter(description = "댓글을 작성할 게시글 ID") @PathVariable("postId") UUID postId,
            @AuthenticationPrincipal WhalesUserPrincipal principal,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        CommentResponse comment = commentService.createComment(postId, principal.getId(), request);
        return ResponseEntity.ok(comment);
    }

    @Operation(
            summary = "댓글 수정",
            description = "로그인한 사용자가 작성한 댓글만 수정할 수 있습니다."
    )
    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @Parameter(description = "수정할 댓글 ID") @PathVariable("id") UUID id,
            @AuthenticationPrincipal WhalesUserPrincipal principal,
            @Valid @RequestBody UpdateCommentRequest request
    ) {
        CommentResponse comment = commentService.updateComment(id, principal.getId(), request);
        return ResponseEntity.ok(comment);
    }

    @Operation(
            summary = "댓글 삭제",
            description = """
                    softDelete=true → 삭제된 것으로 표시만 하고 실제 데이터는 유지합니다.
                    softDelete=false → DB에서 실제 삭제됩니다.
                    """
    )
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "삭제할 댓글 ID") @PathVariable("id") UUID id,
            @AuthenticationPrincipal WhalesUserPrincipal principal,
            @RequestParam(name = "hard", defaultValue = "false")
            @Parameter(description = "true일 경우 완전 삭제, false면 소프트 삭제") boolean hard
    ) {
        commentService.deleteComment(id, principal.getId(), hard);
        return ResponseEntity.noContent().build();
    }
}