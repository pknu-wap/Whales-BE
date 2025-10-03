package com.whales.comment.api;

import com.whales.comment.application.CommentService;
import com.whales.security.WhalesUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> listCommentByPost(@PathVariable("postId") UUID postId) {
        List<CommentResponse> list = commentService.listByPost(postId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable("id") UUID id) {
        CommentResponse comment = commentService.getById(id);
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable("postId") UUID postId,
                                                         @AuthenticationPrincipal WhalesUserPrincipal principal,
                                                         @Valid @RequestBody CreateCommentRequest request) {
        CommentResponse comment = commentService.createComment(postId, principal.getId(), request);
        return ResponseEntity.ok(comment);
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("id") UUID id,
                                                         @AuthenticationPrincipal WhalesUserPrincipal principal,
                                                         @Valid @RequestBody UpdateCommentRequest request) {
        CommentResponse comment = commentService.updateComment(id, principal.getId(), request);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") UUID id,
                                              @AuthenticationPrincipal WhalesUserPrincipal principal,
                                              @RequestParam(name = "hard", defaultValue = "false") boolean hard) {
        commentService.deleteComment(id, principal.getId(), hard);
        return ResponseEntity.noContent().build();
    }
}
