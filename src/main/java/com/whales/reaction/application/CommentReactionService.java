package com.whales.reaction.application;

import com.whales.comment.domain.Comment;
import com.whales.comment.domain.CommentRepository;
import com.whales.reaction.api.ReactionSummary;
import com.whales.reaction.domain.CommentReaction;
import com.whales.reaction.domain.CommentReactionRepository;
import com.whales.reaction.domain.ReactionType;
import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentReactionService extends ReactionService<CommentReaction> {

    private final CommentReactionRepository commentReactionRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void toggle(UUID commentId, UUID userId, ReactionType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        Optional<CommentReaction> existing = commentReactionRepository.findByUser_IdAndComment_Id(userId, commentId);
        toggleReaction(existing , new CommentReaction(user, comment, type), type);
    }

    @Transactional(readOnly = true)
    public ReactionSummary getReactionSummary(UUID commentId, UUID userId) {
        return aggregateReactionSummary(commentId, userId);
    }

    @Override
    protected Optional<CommentReaction> findReactionByUserAndTargetId(UUID userId, UUID commentId) {
        return commentReactionRepository.findByUser_IdAndComment_Id(userId, commentId);
    }

    @Override
    protected void saveReaction(CommentReaction reaction) {
        commentReactionRepository.save(reaction);
    }

    @Override
    protected void deleteReaction(CommentReaction reaction) {
        commentReactionRepository.delete(reaction);
    }

    @Override
    protected ReactionSummary aggregateReactionSummary(UUID commentId, UUID userId) {
        Object[] result = commentReactionRepository.getReactionSummary(commentId, userId);

        long likeCount = result[0] != null ? ((Number) result[0]).longValue() : 0L;
        long dislikeCount = result[1] != null ? ((Number) result[1]).longValue() : 0L;
        ReactionType reactionType = result[2] != null ? ReactionType.valueOf(result[2].toString()) : null;

        return new ReactionSummary(likeCount, dislikeCount, reactionType);
    }
}
