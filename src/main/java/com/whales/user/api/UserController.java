package com.whales.user.api;

import com.whales.user.application.UserService;
import com.whales.security.WhalesUserPrincipal;
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
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal WhalesUserPrincipal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");

        MeResponse dto = userService.getProfile(principal.getId());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me")
    public ResponseEntity<MeResponse> updateMe(@AuthenticationPrincipal WhalesUserPrincipal principal,
                                               @Valid @RequestBody UpdateProfileRequest request) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");

        MeResponse dto = userService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(dto);
    }
}
