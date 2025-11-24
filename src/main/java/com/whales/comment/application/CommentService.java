package com.whales.comment.application;

import com.whales.comment.api.CommentResponse;
import com.whales.comment.api.CreateCommentRequest;
import com.whales.comment.api.UpdateCommentRequest;
import com.whales.comment.domain.Comment;
import com.whales.comment.domain.CommentRepository;
import com.whales.common.ContentStatus;
import com.whales.notification.application.NotificationService;
import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.reaction.api.ReactionSummary;
import com.whales.reaction.application.CommentReactionService;
import com.whales.user.application.UserMetricsService;
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
    private final NotificationService notificationService;
    private final UserMetricsService userMetricsService;

    /**
     * 게시글 기준 댓글 목록 조회
     * BLOCKED 댓글 제외
     */
    public List<CommentResponse> listByPost(UUID postId, UUID userId) {

        List<Comment> comments = commentRepository
                .findByPost_IdAndDeletedAtIsNullAndStatusOrderByCreatedAtDesc(
                        postId,
                        ContentStatus.ACTIVE
                );

        return comments.stream()
                .map(c -> {
                    ReactionSummary reactions = commentReactionService.getReactionSummary(c.getId(), userId);
                    return CommentResponse.from(c, reactions);
                })
                .collect(Collectors.toList());
    }

    /**
     * 댓글 단건 조회
     * BLOCKED 댓글은 접근 불가
     */
    public CommentResponse getById(UUID commentId, UUID userId) {
        Comment comment = loadComment(commentId);

        if (comment.getStatus() == ContentStatus.BLOCKED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Comment is blocked by moderation");
        }

        ReactionSummary reactions = commentReactionService.getReactionSummary(commentId, userId);
        return CommentResponse.from(comment, reactions);
    }

    /**
     * 내가 쓴 댓글 목록
     * BLOCKED 제외
     */
    public List<CommentResponse> listByUserId(UUID authorId) {
        List<Comment> comments = commentRepository
                .findByAuthor_IdAndDeletedAtIsNullAndStatusOrderByCreatedAtDesc(
                        authorId,
                        ContentStatus.ACTIVE
                );

        return comments.stream()
                .map(c -> {
                    ReactionSummary reactions =
                            commentReactionService.getReactionSummary(c.getId(), authorId);
                    return CommentResponse.from(c, reactions);
                })
                .collect(Collectors.toList());
    }

    /**
     * 댓글 생성
     */
    @Transactional
    public CommentResponse createComment(UUID postId, UUID authorId, CreateCommentRequest request) {

        Post post = loadPost(postId);
        User author = loadUser(authorId);

        Comment comment = new Comment(post, author, request.body());
        Comment saved = commentRepository.save(comment);

        // 알림: 작성자 본인이 아니면
        if (!post.getAuthor().getId().equals(authorId)) {
            notificationService.notifyNewComment(post, saved);
        }

        // Metrics 증가
        userMetricsService.increaseCommentCount(authorId);

        return CommentResponse.from(saved, new ReactionSummary(0, 0, null));
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CommentResponse updateComment(UUID commentId, UUID authorId, UpdateCommentRequest request) {

        Comment comment = loadComment(commentId);

        ensureAuthor(comment, authorId);

        if (comment.getStatus() == ContentStatus.BLOCKED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Blocked comment cannot be edited");
        }

        comment.setBody(request.body());
        Comment updated = commentRepository.save(comment);

        ReactionSummary reactions = commentReactionService.getReactionSummary(commentId, authorId);
        return CommentResponse.from(updated, reactions);
    }

    /**
     * 댓글 삭제
     * softDelete = true → 숨김 처리
     * softDelete = false → 완전 삭제
     */
    @Transactional
    public void deleteComment(UUID commentId, UUID requesterId, boolean softDelete) {

        Comment comment = loadComment(commentId);
        ensureAuthor(comment, requesterId);

        if (comment.getStatus() == ContentStatus.BLOCKED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Blocked comment cannot be deleted");
        }

        if (softDelete) {
            comment.setDeletedAt(Instant.now());
            comment.setStatus(ContentStatus.DELETED);
            commentRepository.save(comment);
        } else {
            commentRepository.delete(comment);
        }
    }

    // ---------- Helpers ----------
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the author can manage this comment");
        }
    }
}