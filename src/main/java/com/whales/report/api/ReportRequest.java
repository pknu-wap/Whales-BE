package com.whales.report.api;

import com.whales.report.domain.ReportReason;
import jakarta.validation.constraints.NotNull;

public record ReportRequest(
        @NotNull ReportReason reason,
        String detail
) {
}
