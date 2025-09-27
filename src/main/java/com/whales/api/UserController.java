package com.whales.api;

import com.whales.api.dto.request.UpdateProfileRequest;
import com.whales.api.dto.response.MeResponse;
import com.whales.domain.user.UserService;
import com.whales.security.WhalesUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal WhalesUserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        MeResponse dto = userService.getProfile(principal.getId());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me")
    public ResponseEntity<MeResponse> updateMe(@AuthenticationPrincipal WhalesUserPrincipal principal,
                                               @Valid @RequestBody UpdateProfileRequest request) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        MeResponse dto = userService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(dto);
    }
}
