package com.whales.domain.post;

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
    public ResponseEntity<Post> createPost(@RequestParam Long userId,
                                           @RequestParam String title,
                                           @RequestParam String content) {
        return ResponseEntity.ok(postService.createPost(userId, title, content));
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
