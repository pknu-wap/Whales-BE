package com.whales.post.application;

import com.whales.post.api.CreatePostRequest;
import com.whales.post.api.PostResponse;
import com.whales.post.api.UpdatePostRequest;
import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.tag.api.TagListRequest;
import com.whales.tag.application.TagService;
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
    private final TagService tagService;

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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Post not found with id: " + id));
        return PostResponse.from(post);
    }

    /**
     * 게시글 생성 + 태그 처리
     */
    @Transactional
    public PostResponse createPost(UUID authorId, CreatePostRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Author not found with id: " + authorId));

        Post newPost = new Post();
        newPost.setTitle(request.title());
        newPost.setContent(request.content());
        newPost.setAuthor(author);

        Post saved = postRepository.save(newPost);

        if (request.tags() != null && !request.tags().isEmpty()) {
            tagService.addTags(saved.getId(), authorId, new TagListRequest(request.tags()));
        }

        // 태그 반영된 최신 엔티티 다시 조회
        Post refreshed = postRepository.findById(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return PostResponse.from(saved);
    }

    /**
     * 게시글 수정 + 태그 교체
     */
    @Transactional
    public PostResponse updatePost(UUID id, UUID authorId, UpdatePostRequest request) {
        Post post = loadPostWithAuth(id, authorId);

        if (request.title() != null) post.setTitle(request.title());
        if (request.content() != null) post.setContent(request.content());

        Post saved = postRepository.save(post);

        if (request.tags() != null) {
            tagService.replaceAllTags(saved.getId(), authorId, new TagListRequest(request.tags()));
        }

        Post refreshed = postRepository.findById(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return PostResponse.from(saved);
    }

    // 삭제
    @Transactional
    public void deletePost(UUID id, UUID authorId) {
        Post post = loadPostWithAuth(id, authorId);
        postRepository.delete(post);
    }

    private Post loadPostWithAuth(UUID postId, UUID authorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (post.getAuthor() == null || !post.getAuthor().getId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only author can manage posts");
        }
        return post;
    }
}