package com.whales.tag.api;

import com.whales.post.api.PostResponse;
import com.whales.security.WhalesUserPrincipal;
import com.whales.tag.application.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class TagController {

    private final TagService tagService;

    // 게시글의 태그 목록
    @GetMapping("/posts/{postId}/tags")
    public ResponseEntity<List<TagResponse>> getTagsByPostId(@PathVariable UUID postId) {
        return ResponseEntity.ok(tagService.listByPost(postId));
    }

    // 태그 여러 개 추가
    @PostMapping("/posts/{postId}/tags")
    public ResponseEntity<List<TagResponse>> addTags(@PathVariable UUID postId,
                                                     @AuthenticationPrincipal WhalesUserPrincipal principal,
                                                     @Valid @RequestBody TagListRequest request) {
        return ResponseEntity.ok(tagService.addTags(postId, principal.getId(), request));
    }

    // 태그 한 개 추가
    @PostMapping("/posts/{postId}/tags/one")
    public ResponseEntity<TagResponse> addOneTag(@PathVariable UUID postId,
                                              @AuthenticationPrincipal WhalesUserPrincipal principal,
                                              @Valid @RequestBody TagRequest request) {
        return ResponseEntity.ok(tagService.addOneTag(postId, principal.getId(), request));
    }

    // 태그 한 개 제거
    @DeleteMapping("/posts/{postId}/tags/{tagId}")
    public ResponseEntity<Void> removeOneTag(@PathVariable UUID postId,
                                          @PathVariable UUID tagId,
                                          @AuthenticationPrincipal WhalesUserPrincipal principal) {
        tagService.removeOneTag(postId, tagId, principal.getId());
        return ResponseEntity.noContent().build();
    }

    // 태그 전체 교체 (덮어쓰기)
    @PutMapping("/posts/{postId}/tags")
    public ResponseEntity<List<TagResponse>> replaceTags(@PathVariable UUID postId,
                                                         @AuthenticationPrincipal WhalesUserPrincipal principal,
                                                         @Valid @RequestBody TagListRequest request) {
        return ResponseEntity.ok(tagService.replaceAllTags(postId, principal.getId(), request));
    }

    // 태그로 게시글 검색
    @GetMapping("/tags/search")
    public ResponseEntity<List<PostResponse>> searchPostsByTags(@RequestParam("names") List<String> names) {
        List<PostResponse> posts = tagService.getPostsByTags(names);
        return ResponseEntity.ok(posts);
    }

}
