package com.whales.reaction.api;

import com.whales.reaction.application.CommentReactionService;
import com.whales.reaction.domain.ReactionType;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentReactionController {

    private final CommentReactionService commentReactionService;

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> toggleLike(@PathVariable UUID commentId,
                                     @AuthenticationPrincipal WhalesUserPrincipal principal) {
        commentReactionService.toggle(commentId, principal.getId(), ReactionType.LIKE);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/dislike")
    public ResponseEntity<Void> toggleDislike(@PathVariable UUID commentId,
                              @AuthenticationPrincipal WhalesUserPrincipal principal) {
        commentReactionService.toggle(commentId, principal.getId(), ReactionType.DISLIKE);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{commentId}/reactions")
    public ResponseEntity<ReactionSummary> getCommentReactionSummary(@PathVariable UUID commentId,
                                                     @AuthenticationPrincipal WhalesUserPrincipal principal) {
        ReactionSummary reactions = commentReactionService.getReactionSummary(commentId, principal.getId());
        return ResponseEntity.ok(reactions);
    }
}
