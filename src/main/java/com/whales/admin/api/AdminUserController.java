package com.whales.admin.api;

import com.whales.admin.application.AdminUserService;
import com.whales.security.WhalesUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 사용자 정지
     */
    @PostMapping("/{userId}/ban")
    public ResponseEntity<Void> banUser(@AuthenticationPrincipal WhalesUserPrincipal principal,
                                        @PathVariable UUID userId,
                                        @RequestParam(required = false) String reason) {
        adminUserService.banUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자 정지 해제
     */
    @PostMapping("/{userId}/unban")
    public ResponseEntity<Void> unbanUser(
            @AuthenticationPrincipal WhalesUserPrincipal principal,
            @PathVariable UUID userId
    ) {
        adminUserService.unbanUser(userId);
        return ResponseEntity.noContent().build();
    }
}