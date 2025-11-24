package com.whales.admin.api;

import com.whales.admin.application.AdminReportService;
import com.whales.report.domain.ReportStatus;
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
public class AdminReportController {

    private final AdminReportService reportService;

    // 전체 신고 조회
    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    // 특정 대상(Post or Comment)에 대한 모든 신고 조회
    @GetMapping("/{targetId}")
    public ResponseEntity<List<ReportResponse>> getReportsByReportId(@PathVariable UUID targetId) {
        return ResponseEntity.ok(reportService.getReportsByTargetId(targetId));
    }

    // 상태별 조회 (PENDING / ACCEPTED / REJECTED)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReportResponse>> getReportsByStatus(@PathVariable ReportStatus status) {
        return ResponseEntity.ok(reportService.getReportsByStatus(status));
    }

    // 신고 상세 조회
    @GetMapping("/{id}/detail")
    public ResponseEntity<ReportResponse> getReportDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.getReportDetail(id));
    }

    // 신고 처리
    @PatchMapping("/{id}/process")
    public ResponseEntity<Void> processReport(@PathVariable UUID id,
                                              @RequestBody ReportProcessRequest request) {
        reportService.processReport(id, request.status(), request.note());
        return ResponseEntity.noContent().build();
    }
}