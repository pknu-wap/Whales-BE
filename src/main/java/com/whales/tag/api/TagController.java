package com.whales.tag.api;

import com.whales.post.api.PostResponse;
import com.whales.security.WhalesUserPrincipal;
import com.whales.tag.application.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tag API", description = "게시글 태그 관리 및 태그 기반 검색 API")
public class TagController {

    private final TagService tagService;

    @Operation(
            summary = "게시글 태그 목록 조회",
            description = "지정한 게시글(postId)에 연결된 태그 리스트를 반환합니다."
    )
    @GetMapping("/posts/{postId}/tags")
    public ResponseEntity<List<TagResponse>> getTagsByPostId(
            @Parameter(description = "게시글 ID")
            @PathVariable UUID postId
    ) {
        return ResponseEntity.ok(tagService.listByPost(postId));
    }

    @Operation(
            summary = "여러 개의 태그 추가",
            description = "게시글에 태그 리스트를 한 번에 추가합니다."
    )
    @PostMapping("/posts/{postId}/tags")
    public ResponseEntity<List<TagResponse>> addTags(
            @Parameter(description = "게시글 ID")
            @PathVariable UUID postId,

            @AuthenticationPrincipal WhalesUserPrincipal principal,

            @Valid @RequestBody TagListRequest request
    ) {
        return ResponseEntity.ok(tagService.addTags(postId, principal.getId(), request));
    }

    @Operation(
            summary = "단일 태그 추가",
            description = "게시글에 태그 한 개를 추가합니다."
    )
    @PostMapping("/posts/{postId}/tags/one")
    public ResponseEntity<TagResponse> addOneTag(
            @Parameter(description = "게시글 ID")
            @PathVariable UUID postId,

            @AuthenticationPrincipal WhalesUserPrincipal principal,

            @Valid @RequestBody TagRequest request
    ) {
        return ResponseEntity.ok(tagService.addOneTag(postId, principal.getId(), request));
    }

    @Operation(
            summary = "단일 태그 제거",
            description = "게시글에서 특정 태그(tagId)를 제거합니다."
    )
    @DeleteMapping("/posts/{postId}/tags/{tagId}")
    public ResponseEntity<Void> removeOneTag(
            @Parameter(description = "게시글 ID")
            @PathVariable UUID postId,

            @Parameter(description = "태그 ID")
            @PathVariable UUID tagId,

            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        tagService.removeOneTag(postId, principal.getId(), tagId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "태그 전체 교체",
            description = "게시글의 태그 목록을 새로운 리스트로 통째로 교체합니다."
    )
    @PutMapping("/posts/{postId}/tags")
    public ResponseEntity<List<TagResponse>> replaceTags(
            @Parameter(description = "게시글 ID")
            @PathVariable UUID postId,

            @AuthenticationPrincipal WhalesUserPrincipal principal,

            @Valid @RequestBody TagListRequest request
    ) {
        return ResponseEntity.ok(tagService.replaceAllTags(postId, principal.getId(), request));
    }

    @Operation(
            summary = "태그 기반 게시글 검색",
            description = "여러 태그 이름 리스트를 기반으로 모든 태그를 포함한 게시글만 검색합니다."
    )
    @GetMapping("/posts/by-tags")
    public ResponseEntity<List<PostResponse>> searchPostsByTags(
            @Parameter(description = "검색할 태그 이름 리스트")
            @RequestParam("names") List<String> names
    ) {
        return ResponseEntity.ok(tagService.getPostsByTags(names));
    }

    @Operation(
            summary = "태그 자동완성",
            description = "키워드를 기반으로 태그 자동완성 목록을 반환합니다."
    )
    @GetMapping("/tags/autocomplete")
    public ResponseEntity<List<TagResponse>> searchTags(
            @Parameter(description = "검색 키워드")
            @RequestParam("keyword") String keyword,

            @Parameter(description = "최대 반환 개수 (기본 5)")
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(tagService.autoComplete(keyword, limit));
    }
}