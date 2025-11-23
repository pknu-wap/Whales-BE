package com.whales.post.application;

import com.whales.comment.domain.CommentRepository;
import com.whales.post.api.CreatePostRequest;
import com.whales.post.api.PostResponse;
import com.whales.post.api.UpdatePostRequest;
import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.reaction.api.ReactionSummary;
import com.whales.reaction.application.PostReactionService;
import com.whales.tag.api.TagListRequest;
import com.whales.tag.application.TagService;
import com.whales.user.application.UserMetricsService;
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
    private final PostReactionService postReactionService;
    private final UserMetricsService userMetricsService;
    private final CommentRepository commentRepository;

    // 전체 조회
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(post -> {
                    long commentCount = commentRepository.countByPost_Id(post.getId());
                    ReactionSummary reactions = postReactionService.getReactionSummaryWithoutUser(post.getId());

                    return PostResponse.from(post, commentCount, reactions);
                })
                .collect(Collectors.toList());
    }

    // 상세 조회
    public PostResponse getPostById(UUID postId, UUID userId) {
        Post post = loadPost(postId);

        long commentCount = commentRepository.countByPost_Id(post.getId());
        ReactionSummary reactions = postReactionService.getReactionSummary(postId, userId);
        return PostResponse.from(post, commentCount, reactions);
    }

    /**
     * 게시글 생성 + 태그 처리
     */
    @Transactional
    public PostResponse createPost(UUID authorId, CreatePostRequest request) {
        User author = loadAuthor(authorId);

        Post newPost = new Post();
        newPost.setTitle(request.title());
        newPost.setContent(request.content());
        newPost.setAuthor(author);

        Post saved = postRepository.save(newPost);

        if (request.tags() != null && !request.tags().isEmpty()) {
            tagService.addTags(saved.getId(), authorId, new TagListRequest(request.tags()));
        }

        // 태그 반영된 최신 엔티티 다시 조회
        Post refreshed = postRepository.findByIdWithTagsAndAuthor(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        userMetricsService.increasePostCount(authorId);

        long commentCount = 0;
        ReactionSummary reactions = postReactionService.getReactionSummaryWithoutUser(refreshed.getId());
        return PostResponse.from(refreshed, commentCount, reactions);
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

        Post refreshed = postRepository.findByIdWithTagsAndAuthor(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        long commentCount = commentRepository.countByPost_Id(refreshed.getId());
        ReactionSummary reactions = postReactionService.getReactionSummaryWithoutUser(refreshed.getId());
        return PostResponse.from(refreshed, commentCount, reactions);
    }

    // 삭제
    @Transactional
    public void deletePost(UUID id, UUID authorId) {
        Post post = loadPostWithAuth(id, authorId);
        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> searchPosts(String query) {
        List<Post> results = postRepository.searchByKeyword(query);

        return results.stream()
                .map(post -> {
                    long commentCount = commentRepository.countByPost_Id(post.getId());
                    ReactionSummary reactions = postReactionService.getReactionSummaryWithoutUser(post.getId());

                    return PostResponse.from(post, commentCount, reactions);
                })
                .collect(Collectors.toList());
    }

    // ---------- helpers ----------
    private Post loadPostWithAuth(UUID postId, UUID authorId) {
        Post post = postRepository.findByIdWithTagsAndAuthor(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        if (post.getAuthor() == null || !post.getAuthor().getId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only author can manage posts");
        }
        return post;
    }

    private Post loadPost(UUID postId) {
        return postRepository.findByIdWithTagsAndAuthor(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    private User loadAuthor(UUID authorId) {
        return userRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
    }
}