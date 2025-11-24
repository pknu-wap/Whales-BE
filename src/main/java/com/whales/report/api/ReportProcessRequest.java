package com.whales.report.api;

import com.whales.report.domain.ReportStatus;
import jakarta.validation.constraints.NotNull;

public record ReportProcessRequest(
        @NotNull ReportStatus status,
        String note
) {
}
