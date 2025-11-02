package com.whales.reaction.api;

import com.whales.reaction.application.PostReactionService;
import com.whales.reaction.domain.ReactionType;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostReactionController {

    private final PostReactionService postReactionService;

    @PostMapping("/{postId}/like")
    public void toggleLike(@PathVariable UUID postId,
                           @AuthenticationPrincipal WhalesUserPrincipal principal) {
        postReactionService.toggle(postId, principal.getId(), ReactionType.LIKE);
    }

    @PostMapping("/{postId}/dislike")
    public void toggleDislike(@PathVariable UUID postId,
                              @AuthenticationPrincipal WhalesUserPrincipal principal) {
        postReactionService.toggle(postId, principal.getId(), ReactionType.DISLIKE);
    }
}
