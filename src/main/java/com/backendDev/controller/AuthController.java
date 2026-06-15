package com.backendDev.controller;

import com.backendDev.dto.*;
import com.backendDev.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * STEP 1 — Initiate registration.
     * Saves basic user info, generates OTP, sends to email.
     *
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        ApiResponse<RegisterResponse> response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * STEP 2 — Verify OTP.
     * Validates OTP, marks email as verified.
     *
     * POST /api/auth/verify-otp
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        ApiResponse<String> response = authService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    /**
     * STEP 3 — Complete registration.
     * Sets BCrypt password, generates adminId, returns adminId.
     *
     * POST /api/auth/create-password
     */
    @PostMapping("/create-password")
    public ResponseEntity<ApiResponse<CreatePasswordResponse>> createPassword(
            @Valid @RequestBody CreatePasswordRequest request) {
        ApiResponse<CreatePasswordResponse> response = authService.createPassword(request);
        return ResponseEntity.ok(response);
    }
}
