package com.whales.scrap.api;

import com.whales.post.api.PostResponse;
import com.whales.scrap.application.PostScrapService;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
                .map(PostResponse::from)
                .toList();

        return ResponseEntity.ok(myScraps);
    }
}