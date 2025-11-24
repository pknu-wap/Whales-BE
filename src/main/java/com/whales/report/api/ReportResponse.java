package com.whales.report.api;

import com.whales.report.domain.Report;
import com.whales.report.domain.ReportStatus;
import com.whales.report.domain.ReportTargetType;

import java.time.Instant;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        UUID reporterId,
        ReportTargetType targetType,
        UUID targetId,
        String reason,
        ReportStatus status,
        Instant createdAt
) {
    public static ReportResponse from(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getReporter().getId(),
                report.getTargetType(),
                report.getTargetId(),
                report.getReason(),
                report.getStatus(),
                report.getCreatedAt()
        );
    }
}
