package com.backendDev.controller;

import com.backendDev.constants.ApiConstants;
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

    @PostMapping(ApiConstants.REGISTER_USER)
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping(ApiConstants.VERIFY_OTP)
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping(ApiConstants.CREATE_PASSWORD)
    public ResponseEntity<ApiResponse<CreatePasswordResponse>> createPassword(
            @Valid @RequestBody CreatePasswordRequest request) {
        return ResponseEntity.ok(authService.createPassword(request));
    }

    @PostMapping(ApiConstants.LOGIN_USER)
    public ResponseEntity<LoginApiResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PutMapping("/change-password/{adminId}")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable String adminId,
            @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(adminId, request));
    }

    @DeleteMapping("/delete-account/{adminId}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @PathVariable String adminId) {
        return ResponseEntity.ok(authService.deleteAccount(adminId));
    }
}