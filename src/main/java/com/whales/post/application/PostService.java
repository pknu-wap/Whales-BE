package com.whales.post.application;

import com.whales.post.api.CreatePostRequest;
import com.whales.post.api.PostRequest;
import com.whales.post.api.PostResponse;
import com.whales.post.api.UpdatePostRequest;
import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 전체 조회
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::toPostResponse)
                .collect(Collectors.toList());
    }

    // 상세 조회
    public PostResponse getPostById(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Post not found with id: " + id));
        return toPostResponse(post);
    }

    /**
     * 게시글 생성
     */
    @Transactional
    public PostResponse createPost(UUID authorId, CreatePostRequest request) {
        if (authorId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must be provided in the request body");
        }

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Author not found with id: " + authorId));

        Post newPost = new Post();
        newPost.setTitle(request.title());
        newPost.setContent(request.content());
        newPost.setAuthor(author);

        Post saved = postRepository.save(newPost);
        return toPostResponse(saved);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public PostResponse updatePost(UUID id, UpdatePostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Post not found with id: " + id));

        if (request.title() != null) post.setTitle(request.title());
        if (request.content() != null) post.setContent(request.content());

        Post saved = postRepository.save(post);
        return toPostResponse(saved);
    }

    // 삭제
    @Transactional
    public void deletePost(UUID id) {
        if (!postRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }

    private PostResponse toPostResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getDisplayName(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}