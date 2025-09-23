package com.whales.domain.post;

import com.whales.domain.user.User;
import com.whales.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
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

    public void deletePost(UUID id) {
        // ID로 게시물을 찾아옵니다. (삭제되지 않은 게시물만)
        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시물이 없습니다."));

        // deletedAt 필드에 현재 시간을 설정하여 논리적으로 삭제합니다.
        post.setDeletedAt(Instant.now());
        postRepository.save(post); // UPDATE 쿼리 실행됨
    }
}
