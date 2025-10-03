package com.whales.post.api;

import com.whales.post.application.PostService;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시물 전체 조회
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 게시물 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable UUID postId) {
        PostResponse post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    /**
     * 게시물 생성: PostRequest DTO를 받아 서비스로 전달합니다.
     * 작성자 ID는 DTO 본문(userId)에서 직접 가져옵니다.
     */
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@AuthenticationPrincipal WhalesUserPrincipal principal,
                                                   @RequestBody CreatePostRequest request) {
        PostResponse newPost = postService.createPost(principal.getId(), request);
        return ResponseEntity.ok(newPost);
    }

    /**
     * 게시물 수정: PostRequest DTO를 사용하여 업데이트 데이터를 안전하게 받습니다.
     */
    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable UUID postId,
            @RequestBody UpdatePostRequest request
    ) {
        PostResponse updated = postService.updatePost(postId, request);
        return ResponseEntity.ok(updated);
    }

    // 게시물 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}