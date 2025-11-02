package com.whales.scrap.api;

import com.whales.post.api.PostResponse;
import com.whales.scrap.application.PostScrapService;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostScrapController {

    private final PostScrapService postScrapService;

    @PostMapping("/{postId}/scrap")
    public void toggleScrap(@PathVariable UUID postId,
                            @AuthenticationPrincipal WhalesUserPrincipal principal) {
        postScrapService.toggle(postId, principal.getId());
    }

    @GetMapping("/{postId}/scrap")
    public boolean isScrapped(@PathVariable UUID postId,
                              @AuthenticationPrincipal WhalesUserPrincipal principal) {
        return postScrapService.isScrapped(postId, principal.getId());
    }

    @GetMapping("/scraps")
    public List<PostResponse> getMyScraps(@AuthenticationPrincipal WhalesUserPrincipal principal) {
        return postScrapService.getMyScraps(principal.getId())
                .stream()
                .map(PostResponse::from)
                .toList();
    }
}