package com.whales.report.application;

import com.whales.comment.domain.Comment;
import com.whales.comment.domain.CommentRepository;
import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.report.api.ReportRequest;
import com.whales.report.domain.Report;
import com.whales.report.domain.ReportRepository;
import com.whales.report.domain.ReportTargetType;
import com.whales.user.domain.User;
import com.whales.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void reportPost(UUID reporterId, UUID postId, ReportRequest request) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporter not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));


        if (reportRepository.existsByReporter_IdAndTargetTypeAndTargetId(
                reporterId, ReportTargetType.POST, postId
        )) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 신고한 게시글입니다.");
        }

        Report report = new Report(reporter, ReportTargetType.POST, post.getId(), request.reason(), request.detail());
        reportRepository.save(report);
    }

    @Transactional
    public void reportComment(UUID reporterId, UUID commentId, ReportRequest request) {
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporter not found"));

        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (reportRepository.existsByReporter_IdAndTargetTypeAndTargetId(
                reporterId, ReportTargetType.COMMENT, commentId
        )) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 신고한 댓글입니다.");
        }

        Report report = new Report(reporter, ReportTargetType.COMMENT, comment.getId(), request.reason(), request.detail());
        reportRepository.save(report);
    }
}
