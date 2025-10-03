package com.whales.comment.application;

import com.whales.comment.api.CommentResponse;
import com.whales.comment.api.CreateCommentRequest;
import com.whales.comment.api.UpdateCommentRequest;
import com.whales.comment.domain.Comment;
import com.whales.comment.domain.CommentRepository;
import com.whales.common.ContentStatus;
import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
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

    public List<CommentResponse> listByPost (UUID postId) {

        List<Comment> commentList = commentRepository
                .findByPost_IdAndDeletedAtIsNullAndStatusOrderByCreatedAtDesc(postId, ContentStatus.ACTIVE);

        return commentList.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    public CommentResponse getById(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        return CommentResponse.from(comment);
    }

    @Transactional
    public CommentResponse createComment(UUID postId, UUID authorId, CreateCommentRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(user);
        comment.setBody(request.body());
        comment.setStatus(ContentStatus.ACTIVE);

        Comment saved = commentRepository.save(comment);
        return CommentResponse.from(saved);
    }

    @Transactional
    public CommentResponse updateComment(UUID commentId, UUID authorId, UpdateCommentRequest request) {

        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only author can edit this comment");
        }

        comment.setBody(request.body());
        Comment saved = commentRepository.save(comment);
        return CommentResponse.from(saved);
    }

    @Transactional
    public void deleteComment(UUID commentId, UUID requesterId, boolean softDelete) {
        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!comment.getAuthor().getId().equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only author can delete this comment");
        }

        if (softDelete) {
            comment.setDeletedAt(Instant.now());
            comment.setStatus(ContentStatus.DELETED);
            commentRepository.save(comment);
        } else {
            commentRepository.delete(comment);
        }
    }
}
