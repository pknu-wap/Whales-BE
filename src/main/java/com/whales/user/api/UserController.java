package com.whales.user.api;

import com.whales.security.WhalesUserPrincipal;
import com.whales.user.application.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 정보 조회 및 프로필 수정 API")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 사용자의 프로필(닉네임, 이메일, 뱃지, 점수 등)을 조회합니다."
    )
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(
            @AuthenticationPrincipal WhalesUserPrincipal principal
    ) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        UserResponse dto = userService.getProfile(principal.getId());
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "내 정보 수정",
            description = "로그인한 사용자가 자신의 프로필(닉네임, 프로필 이미지 등)을 수정합니다."
    )
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            @AuthenticationPrincipal WhalesUserPrincipal principal,

            @Valid @RequestBody UpdateProfileRequest request
    ) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        UserResponse dto = userService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(dto);
    }
}