package com.whales.admin.api;

import com.whales.admin.application.AdminReportService;
import com.whales.report.domain.ReportStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Report API", description = "관리자용 신고 조회 및 처리 API")
public class AdminReportController {

    private final AdminReportService reportService;

    @Operation(summary = "전체 신고 조회")
    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @Operation(summary = "특정 대상(게시글/댓글)에 대한 신고 조회")
    @GetMapping("/target/{targetId}")
    public ResponseEntity<List<ReportResponse>> getReportsByReportId(@PathVariable UUID targetId) {
        return ResponseEntity.ok(reportService.getReportsByTargetId(targetId));
    }

    @Operation(summary = "상태별 신고 조회")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReportResponse>> getReportsByStatus(@PathVariable ReportStatus status) {
        return ResponseEntity.ok(reportService.getReportsByStatus(status));
    }

    @Operation(summary = "신고 상세 조회")
    @GetMapping("/{id}/detail")
    public ResponseEntity<ReportResponse> getReportDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.getReportDetail(id));
    }

    @Operation(
            summary = "신고 처리",
            description = "관리자가 신고를 승인(차단) 또는 거절 처리합니다."
    )
    @PatchMapping("/{id}/process")
    public ResponseEntity<Void> processReport(@PathVariable UUID id,
                                              @RequestBody ReportProcessRequest request) {
        reportService.processReport(id, request.status(), request.note());
        return ResponseEntity.noContent().build();
    }
}