package com.schulwiki.backend.auth.controller;

import com.schulwiki.backend.auth.dto.AuthResponse;
import com.schulwiki.backend.auth.dto.LoginRequest;
import com.schulwiki.backend.auth.dto.MessageResponse;
import com.schulwiki.backend.auth.dto.RegisterRequest;
import com.schulwiki.backend.auth.security.refreshToken.dto.RefreshTokenRequest;
import com.schulwiki.backend.auth.service.AuthService;
import com.schulwiki.backend.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            @RequestHeader(value = "X-Device-Fingerprint", required = true) String fingerprint) {
        AuthResponse response = authService.login(request, fingerprint);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request,
            @RequestHeader(value = "X-Device-Fingerprint", required = true) String fingerprint) {
        AuthResponse response = authService.refreshAccessToken(request, fingerprint);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(authService.me());
    }
}
