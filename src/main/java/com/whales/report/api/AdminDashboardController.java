package com.whales.report.api;

import com.whales.report.application.AdminDashboardService;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    /**
     * 전체 통계 요약
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getDashboardSummary(
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}