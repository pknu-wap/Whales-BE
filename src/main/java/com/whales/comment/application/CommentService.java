package com.whales.comment.application;

import com.whales.comment.api.CommentResponse;
import com.whales.comment.api.CreateCommentRequest;
import com.whales.comment.api.UpdateCommentRequest;
import com.whales.comment.domain.Comment;
import com.whales.comment.domain.CommentRepository;
import com.whales.common.ContentStatus;
import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.reaction.api.ReactionSummary;
import com.whales.reaction.application.CommentReactionService;
import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentReactionService commentReactionService;

    public List<CommentResponse> listByPost (UUID postId, UUID userId) {
        List<Comment> commentList = commentRepository
                .findByPost_IdAndDeletedAtIsNullAndStatusOrderByCreatedAtDesc(postId, ContentStatus.ACTIVE);

        return commentList.stream()
                .map(comment -> {
                    ReactionSummary reactions = commentReactionService.getReactionSummary(comment.getId(), userId);
                    return CommentResponse.from(comment, reactions);
                })
                .collect(Collectors.toList());
    }

    public CommentResponse getById(UUID commentId, UUID userId) {
        Comment comment = loadComment(commentId);
        ReactionSummary reactions = commentReactionService.getReactionSummary(commentId, userId);
        return CommentResponse.from(comment, reactions);
    }

    @Transactional
    public CommentResponse createComment(UUID postId, UUID authorId, CreateCommentRequest request) {
        Post post = loadPost(postId);
        User author = loadUser(authorId);

        Comment comment = new Comment(post, author, request.body()) ;
        Comment saved = commentRepository.save(comment);

        ReactionSummary emptyReactions = new ReactionSummary(0, 0, null);
        return CommentResponse.from(saved, emptyReactions);
    }

    @Transactional
    public CommentResponse updateComment(UUID commentId, UUID authorId, UpdateCommentRequest request) {
        Comment comment = loadComment(commentId);
        ensureAuthor(comment, authorId);

        comment.setBody(request.body());
        Comment updated = commentRepository.save(comment);

        ReactionSummary reactions = commentReactionService.getReactionSummary(commentId, authorId);
        return CommentResponse.from(updated, reactions);
    }

    @Transactional
    public void deleteComment(UUID commentId, UUID requesterId, boolean softDelete) {
        Comment comment = loadComment(commentId);
        ensureAuthor(comment, requesterId);

        if (softDelete) {
            comment.setDeletedAt(Instant.now());
            comment.setStatus(ContentStatus.DELETED);
            commentRepository.save(comment);
        } else {
            commentRepository.delete(comment);
        }
    }


    // ---------- helpers ----------
    private Comment loadComment(UUID commentId) {
        return commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
    }

    private Post loadPost(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    private User loadUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void ensureAuthor(Comment comment, UUID authorId) {
        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only author can modify this comment");
        }
    }
}
