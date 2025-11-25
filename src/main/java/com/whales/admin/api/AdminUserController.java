package com.whales.admin.api;

import com.whales.admin.application.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin User API", description = "관리자용 사용자 정지 및 복구 API")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(
            summary = "사용자 계정 정지",
            description = "관리자가 특정 유저를 차단(BANNED) 상태로 변경합니다."
    )
    @PostMapping("/{userId}/ban")
    public ResponseEntity<Void> banUser(@PathVariable UUID userId) {
        adminUserService.banUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "사용자 차단 해제",
            description = "관리자가 특정 유저의 정지를 해제합니다."
    )
    @PostMapping("/{userId}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable UUID userId
    ) {
        adminUserService.unbanUser(userId);
        return ResponseEntity.noContent().build();
    }
}