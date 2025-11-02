package com.whales.reaction.api;

import com.whales.reaction.application.CommentReactionService;
import com.whales.reaction.domain.ReactionType;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
