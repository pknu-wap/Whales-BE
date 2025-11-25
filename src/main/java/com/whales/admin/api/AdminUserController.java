package com.whales.admin.api;

import com.whales.admin.application.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Void> banUser(@PathVariable UUID userId) {
        adminUserService.banUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자 정지 해제
     */
    @PostMapping("/{userId}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable UUID userId
    ) {
        adminUserService.unbanUser(userId);
        return ResponseEntity.noContent().build();
    }
}