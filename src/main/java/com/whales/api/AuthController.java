//package com.whales.api;
//
//import com.whales.api.dto.request.LoginRequest;
//import com.whales.api.dto.request.SignupRequest;
//import com.whales.api.dto.response.LoginResponse;
//import com.whales.auth.AuthService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final AuthService authService;
//
//    @PostMapping("/signup")
//    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
//        authService.signup(request);
//        return ResponseEntity.ok("User registered successfully");
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
//        return ResponseEntity.ok(authService.login(request));
//    }
//
//    @PostMapping("/refresh")
//    public ResponseEntity<LoginResponse> refresh(@RequestBody Map<String, String> request) {
//        String refreshToken = request.get("refreshToken");
//        return ResponseEntity.ok(authService.refreshToken(refreshToken));
//    }
//}