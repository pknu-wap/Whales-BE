package com.whales.report.api;

import com.whales.report.domain.ReportReason;
import jakarta.validation.constraints.NotBlank;

public record ReportRequest(
        @NotBlank ReportReason reason,
        String detail
) {
}
