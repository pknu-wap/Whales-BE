package com.whales.scrap.api;

import com.whales.comment.domain.CommentRepository;
import com.whales.post.api.PostResponse;
import com.whales.reaction.api.ReactionSummary;
import com.whales.reaction.application.PostReactionService;
import com.whales.scrap.application.PostScrapService;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostScrapController {

    private final PostScrapService postScrapService;
    private final CommentRepository commentRepository;
    private final PostReactionService postReactionService;

    @PostMapping("/{postId}/scrap")
    public ResponseEntity<Void> toggleScrap(@PathVariable UUID postId,
                     @AuthenticationPrincipal WhalesUserPrincipal principal) {
        postScrapService.toggle(postId, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}/scrap")
    public ResponseEntity<Boolean> isScrapped(@PathVariable UUID postId,
                              @AuthenticationPrincipal WhalesUserPrincipal principal) {
        Boolean scrapped = postScrapService.isScrapped(postId, principal.getId());
        return ResponseEntity.ok(scrapped);
    }

    @GetMapping("/scraps")
    public ResponseEntity<List<PostResponse>> getMyScraps(@AuthenticationPrincipal WhalesUserPrincipal principal) {
        List<PostResponse> myScraps = postScrapService.getMyScraps(principal.getId())
                .stream()
                .map(post -> {
                    long commentCount = commentRepository.countByPost_Id(post.getId());
                    ReactionSummary reactions = postReactionService.getReactionSummaryWithoutUser(post.getId());

                    return PostResponse.from(post, commentCount, reactions);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(myScraps);
    }
}