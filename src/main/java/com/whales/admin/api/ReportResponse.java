package com.whales.admin.api;

import com.whales.report.domain.Report;
import com.whales.report.domain.ReportReason;
import com.whales.report.domain.ReportStatus;
import com.whales.report.domain.ReportTargetType;

import java.time.Instant;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        UUID reporterId,
        String reporterName,
        ReportTargetType targetType,
        UUID targetId,
        String targetSummary,        // 게시글 제목 또는 댓글 일부
        UUID targetAuthorId,
        String targetAuthorName,
        ReportReason reason,
        String detail,
        ReportStatus status,
        Instant createdAt,
        Instant resolvedAt,
        String adminNote
) {
    public static ReportResponse from(Report report, String targetSummary, UUID targetAuthorId, String targetAuthorName) {
        return new ReportResponse(
                report.getId(),
                report.getReporter().getId(),
                report.getReporter().getDisplayName(),
                report.getTargetType(),
                report.getTargetId(),
                targetSummary,
                targetAuthorId,
                targetAuthorName,
                report.getReason(),
                report.getDetail(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getResolvedAt(),
                report.getAdminNote()
        );
    }
}
