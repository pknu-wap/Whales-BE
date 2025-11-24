package com.whales.report.api;

import com.whales.report.application.ReportService;
import com.whales.security.WhalesUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/posts/{postId}")
    public ResponseEntity<Void> reportPost(@PathVariable UUID postId,
                                           @AuthenticationPrincipal WhalesUserPrincipal principal,
                                           @Valid @RequestBody ReportRequest request) {
        reportService.reportPost(principal.getId(), postId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<Void> reportComment(@PathVariable UUID commentId,
                                              @AuthenticationPrincipal WhalesUserPrincipal principal,
                                              @Valid @RequestBody ReportRequest request) {
        reportService.reportComment(principal.getId(), commentId, request);
        return ResponseEntity.ok().build();
    }
}
