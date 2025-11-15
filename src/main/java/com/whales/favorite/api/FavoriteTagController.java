package com.whales.favorite.api;

import com.whales.favorite.application.FavoriteTagService;
import com.whales.security.WhalesUserPrincipal;
import com.whales.tag.api.TagResponse;
import com.whales.tag.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags/favorites")
public class FavoriteTagController {

    private final FavoriteTagService favoriteTagService;

    @PostMapping
    public ResponseEntity<Void> addFavoriteTag(@RequestBody FavoriteTagRequest request,
                                               @AuthenticationPrincipal WhalesUserPrincipal principal) {
        favoriteTagService.addFavoriteTag(principal.getId(), request.name());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteFavoriteTag(@AuthenticationPrincipal WhalesUserPrincipal principal,
                                                  @PathVariable UUID tagId) {
        favoriteTagService.removeFavoriteTag(principal.getId(), tagId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TagResponse>> getMyFavoriteTags(@AuthenticationPrincipal WhalesUserPrincipal principal) {
        List<Tag> tags = favoriteTagService.getMyFavoriteTags(principal.getId());
        List<TagResponse> response = tags.stream()
                .map(TagResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
