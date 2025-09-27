package com.whales.post.application;

import com.whales.post.api.PostRequest;
import com.whales.post.api.PostResponse;
import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository; // User 엔티티를 찾기 위해 필요

    // 전체 조회
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());
    }

    // 상세 조회
    public PostResponse getPostById(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
        return PostResponse.from(post);
    }

    /**
     * 게시글 생성: PostRequest DTO를 받아 DTO 내의 userId를 사용하여 Post 엔티티를 생성합니다.
     */
    @Transactional
    public PostResponse createPost(PostRequest request) {
        // DTO에서 userId를 추출하여 작성자(User) 엔티티를 찾습니다.
        UUID authorId = request.getUserId();
        if (authorId == null) {
            throw new IllegalArgumentException("User ID must be provided in the request body.");
        }

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with id: " + authorId));

        // Post 엔티티를 생성하고 DTO와 User 객체로 데이터를 채웁니다.
        Post newPost = new Post();
        newPost.setTitle(request.getTitle());
        newPost.setContent(request.getContent());
        newPost.setAuthor(author); // 작성자 연결 (author_id NOT NULL 제약조건 해결)

        Post saved = postRepository.save(newPost);
        return PostResponse.from(saved);
    }

    /**
     * 게시글 수정: DTO를 사용하여 안전하게 제목과 내용만 업데이트합니다.
     */
    @Transactional
    public PostResponse updatePost(UUID id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));

        // DTO의 데이터를 사용하여 업데이트
        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getContent() != null) post.setContent(request.getContent());

        Post saved = postRepository.save(post);
        return PostResponse.from(saved);
    }

    // 삭제
    @Transactional
    public void deletePost(UUID id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }
}