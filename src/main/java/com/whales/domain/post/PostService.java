package com.whales.domain.post;

import com.whales.domain.user.User;
import com.whales.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시글 생성
    public Post createPost(Long userId, String title, String content) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = new Post();
        post.setAuthor(author);
        post.setTitle(title);
        post.setContent(content);
        post.setStatus(ContentStatus.ACTIVE);

        return postRepository.save(post);
    }

    // 게시글 수정
    public Post updatePost(UUID postId, String title, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.setTitle(title);
        post.setContent(content);
        post.setUpdatedAt(Instant.now());

        return postRepository.save(post);
    }

    // 게시글 삭제 (Soft delete)
    public void deletePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.setDeletedAt(Instant.now());
        postRepository.save(post);
    }
}
