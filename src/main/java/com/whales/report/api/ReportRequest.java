package com.whales.report.api;

import jakarta.validation.constraints.NotBlank;

public record ReportRequest(
        @NotBlank String reason
) {
}
