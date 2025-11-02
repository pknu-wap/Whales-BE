package com.whales.reaction.api;

import com.whales.reaction.application.PostReactionService;
import com.whales.reaction.domain.ReactionType;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostReactionController {

    private final PostReactionService postReactionService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> toggleLike(@PathVariable UUID postId,
                    @AuthenticationPrincipal WhalesUserPrincipal principal) {
        postReactionService.toggle(postId, principal.getId(), ReactionType.LIKE);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/dislike")
    public ResponseEntity<Void> toggleDislike(@PathVariable UUID postId,
                              @AuthenticationPrincipal WhalesUserPrincipal principal) {
        postReactionService.toggle(postId, principal.getId(), ReactionType.DISLIKE);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/reactions")
    public ResponseEntity<ReactionSummary> getPostReactionSummary(@PathVariable UUID postId,
                                                  @AuthenticationPrincipal WhalesUserPrincipal principal) {
        ReactionSummary reactions = postReactionService.getReactionSummary(postId, principal.getId());
        return ResponseEntity.ok(reactions);
    }
}
