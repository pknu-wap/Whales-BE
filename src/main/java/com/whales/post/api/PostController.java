package com.whales.post.api;

import com.whales.post.application.PostService;
import com.whales.security.WhalesUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Post API", description = "게시글 조회/작성/수정/삭제 API")
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "전체 게시글 조회",
            description = "등록된 모든 게시글을 조회합니다. 댓글 수와 반응 요약 정보가 포함됩니다."
    )
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @Operation(
            summary = "게시글 상세 조회",
            description = "게시글 ID로 상세 정보를 조회합니다. 로그인한 사용자의 반응 정보도 포함됩니다."
    )
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @Parameter(description = "조회할 게시글 ID") @PathVariable UUID postId,
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        UUID userId = (principal != null) ? principal.getId() : null;
        PostResponse post = postService.getPostById(postId, userId);
        return ResponseEntity.ok(post);
    }

    @Operation(
            summary = "게시글 생성",
            description = "새로운 게시글을 작성합니다. 로그인된 사용자만 가능합니다."
    )
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal WhalesUserPrincipal principal,
            @RequestBody CreatePostRequest request
    ) {
        PostResponse newPost = postService.createPost(principal.getId(), request);
        return ResponseEntity.ok(newPost);
    }

    @Operation(
            summary = "게시글 수정",
            description = "기존 게시글을 수정합니다. 작성자만 수정할 수 있습니다."
    )
    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @Parameter(description = "수정할 게시글 ID") @PathVariable UUID postId,
            @AuthenticationPrincipal WhalesUserPrincipal principal,
            @RequestBody UpdatePostRequest request
    ) {
        PostResponse updated = postService.updatePost(postId, principal.getId(), request);
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "게시글 삭제",
            description = """
                    게시글을 삭제합니다.
                    soft delete(기본값)= 데이터 유지하며 삭제 표시  
                    hard delete=true= 완전 삭제
                    """
    )
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "삭제할 게시글 ID") @PathVariable UUID postId,
            @AuthenticationPrincipal WhalesUserPrincipal principal,
            @Parameter(description = "true → 완전 삭제, false → 소프트 삭제")
            @RequestParam(name = "hard", defaultValue = "false") boolean hard
    ) {
        postService.deletePost(postId, principal.getId(), hard);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "게시글 검색",
            description = "제목 또는 내용에 포함된 키워드를 이용해 게시글을 검색합니다."
    )
    @GetMapping("/search")
    public List<PostResponse> searchPosts(
            @Parameter(description = "검색어") @RequestParam("query") String query
    ) {
        return postService.searchPosts(query);
    }
}