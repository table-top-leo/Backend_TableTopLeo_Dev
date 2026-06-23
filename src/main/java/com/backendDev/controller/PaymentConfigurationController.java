package com.backendDev.controller;

import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.PaymentConfigResponse;
import com.backendDev.dto.UpiPaymentRequest;
import com.backendDev.service.PaymentConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Phase 4 — Payment Configuration Controller
 *
 * All endpoints are JWT-protected (except none here — all need auth).
 * admin_id and business_id come automatically from the JWT token — admin NEVER sends them.
 *
 * Base path: /api/payment
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentConfigurationController {

    private final PaymentConfigurationService paymentService;

    /**
     * POST /api/payment/upi/save
     * Save UPI payment configuration.
     * admin_id + business_id are resolved from JWT automatically.
     */
    @PostMapping("/upi/save")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> saveUpiConfig(
            @Valid @RequestBody UpiPaymentRequest request) {
        return ResponseEntity.ok(paymentService.saveUpiConfig(request));
    }

    /**
     * PUT /api/payment/upi/update
     * Update existing UPI configuration.
     */
    @PutMapping("/upi/update")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> updateUpiConfig(
            @Valid @RequestBody UpiPaymentRequest request) {
        return ResponseEntity.ok(paymentService.updateUpiConfig(request));
    }

    /**
     * GET /api/payment/upi
     * Get current admin's UPI configuration.
     */
    @GetMapping("/upi")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> getUpiConfig() {
        return ResponseEntity.ok(paymentService.getUpiConfig());
    }

    /**
     * GET /api/payment/all
     * Get all payment configs for current admin (all payment types).
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PaymentConfigResponse>>> getAllConfigs() {
        return ResponseEntity.ok(paymentService.getAllConfigsByAdmin());
    }

    /**
     * DELETE /api/payment/upi/delete
     * Delete UPI configuration.
     */
    @DeleteMapping("/upi/delete")
    public ResponseEntity<ApiResponse<String>> deleteUpiConfig() {
        return ResponseEntity.ok(paymentService.deleteUpiConfig());
    }
}
