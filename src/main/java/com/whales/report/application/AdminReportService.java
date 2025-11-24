package com.whales.report.application;

import com.whales.comment.domain.Comment;
import com.whales.comment.domain.CommentRepository;
import com.whales.common.ContentStatus;
import com.whales.post.domain.Post;
import com.whales.post.domain.PostRepository;
import com.whales.report.api.ReportResponse;
import com.whales.report.domain.Report;
import com.whales.report.domain.ReportRepository;
import com.whales.report.domain.ReportStatus;
import com.whales.report.domain.ReportTargetType;
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

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserMetricsService userMetricsService;

    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByTargetId(UUID targetId) {
        return reportRepository.findByTargetId(targetId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByStatus(ReportStatus status) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReportResponse getReportDetail(UUID id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

        return toResponse(report);
    }

    @Transactional
    public void processReport(UUID id, ReportStatus status, String adminNote) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Report has already been processed");
        }

        report.setStatus(status);
        report.setAdminNote(adminNote);
        report.setResolvedAt(Instant.now());

        if (status == ReportStatus.ACCEPTED) {
            applyModerationAction(report);
        }
    }

    private void applyModerationAction(Report report) {

        switch (report.getTargetType()) {
            case POST:
                Post post = postRepository.findById(report.getTargetId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
                if (post != null) {
                    post.setStatus(ContentStatus.BLOCKED); // 글 비활성화
                    userMetricsService.increaseReportsCount(post.getAuthor().getId());
                }
                break;

            case COMMENT:
                Comment comment = commentRepository.findById(report.getTargetId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
                if (comment != null) {
                    comment.setStatus(ContentStatus.BLOCKED);
                    userMetricsService.increaseReportsCount(comment.getAuthor().getId());
                }
                break;

//            case USER:
//                User user = userRepository.findById(report.getTargetId())
//                        .orElse(null);
//                if (user != null) {
//                    user.setStatus(UserStatus.BANNED);
//                }
//                break;
        }
    }

    /**
     * Report → ReportResponse 변환
     */
    private ReportResponse toResponse(Report report) {
        String targetSummary = resolveTargetSummary(report);

        // 대상 작성자 정보 가져오기
        User targetAuthor = resolveTargetAuthor(report);

        return ReportResponse.from(
                report,
                targetSummary,
                targetAuthor != null ? targetAuthor.getId() : null,
                targetAuthor != null ? targetAuthor.getDisplayName() : "(알 수 없음)"
        );
    }

    /**
     * 신고 대상의 작성자 식별
     */
    private User resolveTargetAuthor(Report report) {

        if (report.getTargetType() == ReportTargetType.POST) {
            return postRepository.findById(report.getTargetId())
                    .map(Post::getAuthor)
                    .orElse(null);
        }

        if (report.getTargetType() == ReportTargetType.COMMENT) {
            return commentRepository.findById(report.getTargetId())
                    .map(Comment::getAuthor)
                    .orElse(null);
        }

        return null;
    }

    /**
     * 신고 대상 요약 텍스트 생성
     */
    private String resolveTargetSummary(Report report) {
        if (report.getTargetType() == ReportTargetType.POST) {
            return postRepository.findById(report.getTargetId())
                    .map(Post::getTitle)
                    .orElse("(삭제된 게시글)");
        }

        if (report.getTargetType() == ReportTargetType.COMMENT) {
            return commentRepository.findById(report.getTargetId())
                    .map(c -> c.getBody().substring(0, Math.min(30, c.getBody().length())))
                    .orElse("(삭제된 댓글)");
        }

        return "(알 수 없음)";
    }
}
