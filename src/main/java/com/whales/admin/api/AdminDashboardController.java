package com.whales.admin.api;

import com.whales.admin.application.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Dashboard API", description = "관리자 대시보드 통계 조회 API")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @Operation(
            summary = "관리자 대시보드 통계 조회",
            description = """
                    관리자 화면에서 사용하는 통계 데이터를 조회합니다.
                    제공되는 항목은 다음과 같습니다:
                    - pending: 처리 대기 중인 신고 수
                    - accepted: 승인된 신고 수
                    - rejected: 거절된 신고 수
                    - blockedPosts: 차단된 게시글 수
                    - blockedComments: 차단된 댓글 수
                    - orangeUsers: 주의(ORANGE) 등급 유저 수
                    - redUsers: 경고(RED) 등급 유저 수
                    - bannedUsers: 차단된 유저 수
                    """
    )
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}