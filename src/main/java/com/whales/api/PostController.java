package com.whales.api;

import com.whales.api.dto.request.PostRequest;
import com.whales.domain.post.Post;
import com.whales.domain.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable UUID id,
                                           @RequestParam String title,
                                           @RequestParam String content) {
        return ResponseEntity.ok(postService.updatePost(id, title, content));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable UUID id) {
        postService.deletePost(id);
        return ResponseEntity.ok("Post deleted successfully (soft delete)");
    }
}
