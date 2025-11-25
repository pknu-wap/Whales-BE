package com.whales.report.api;

import com.whales.report.application.ReportService;
import com.whales.security.WhalesUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
@Tag(name = "Report API", description = "게시글 및 댓글 신고 관련 API")
public class ReportController {

    private final ReportService reportService;

    @Operation(
            summary = "게시글 신고",
            description = "특정 게시글에 대해 신고를 등록합니다."
    )
    @PostMapping("/posts/{postId}")
    public ResponseEntity<Void> reportPost(@PathVariable UUID postId,
                                           @AuthenticationPrincipal WhalesUserPrincipal principal,
                                           @Valid @RequestBody ReportRequest request) {
        reportService.reportPost(principal.getId(), postId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "댓글 신고",
            description = "특정 댓글에 대해 신고를 등록합니다."
    )
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<Void> reportComment(@PathVariable UUID commentId,
                                              @AuthenticationPrincipal WhalesUserPrincipal principal,
                                              @Valid @RequestBody ReportRequest request) {
        reportService.reportComment(principal.getId(), commentId, request);
        return ResponseEntity.ok().build();
    }
}
