package com.backendDev.controller;

import com.backendDev.dto.*;
import com.backendDev.service.GatewayPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class GatewayPaymentController {

    private final GatewayPaymentService gatewayPaymentService;

    // ── RAZORPAY ────────────────────────────────────────────

    @PostMapping("/razorpay/save")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> saveRazorpay(
            @Valid @RequestBody RazorpayPaymentRequest request) {
        return ResponseEntity.ok(gatewayPaymentService.saveRazorpayConfig(request));
    }

    @PutMapping("/razorpay/update")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> updateRazorpay(
            @Valid @RequestBody RazorpayPaymentRequest request) {
        return ResponseEntity.ok(gatewayPaymentService.updateRazorpayConfig(request));
    }

    @GetMapping("/razorpay")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> getRazorpay() {
        return ResponseEntity.ok(gatewayPaymentService.getRazorpayConfig());
    }

    @DeleteMapping("/razorpay/delete")
    public ResponseEntity<ApiResponse<String>> deleteRazorpay() {
        return ResponseEntity.ok(gatewayPaymentService.deleteRazorpayConfig());
    }

    // ── STRIPE ──────────────────────────────────────────────

    @PostMapping("/stripe/save")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> saveStripe(
            @Valid @RequestBody StripePaymentRequest request) {
        return ResponseEntity.ok(gatewayPaymentService.saveStripeConfig(request));
    }

    @PutMapping("/stripe/update")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> updateStripe(
            @Valid @RequestBody StripePaymentRequest request) {
        return ResponseEntity.ok(gatewayPaymentService.updateStripeConfig(request));
    }

    @GetMapping("/stripe")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> getStripe() {
        return ResponseEntity.ok(gatewayPaymentService.getStripeConfig());
    }

    @DeleteMapping("/stripe/delete")
    public ResponseEntity<ApiResponse<String>> deleteStripe() {
        return ResponseEntity.ok(gatewayPaymentService.deleteStripeConfig());
    }

    // ── PAYPAL ──────────────────────────────────────────────

    @PostMapping("/paypal/save")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> savePaypal(
            @Valid @RequestBody PaypalPaymentRequest request) {
        return ResponseEntity.ok(gatewayPaymentService.savePaypalConfig(request));
    }

    @PutMapping("/paypal/update")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> updatePaypal(
            @Valid @RequestBody PaypalPaymentRequest request) {
        return ResponseEntity.ok(gatewayPaymentService.updatePaypalConfig(request));
    }

    @GetMapping("/paypal")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> getPaypal() {
        return ResponseEntity.ok(gatewayPaymentService.getPaypalConfig());
    }

    @DeleteMapping("/paypal/delete")
    public ResponseEntity<ApiResponse<String>> deletePaypal() {
        return ResponseEntity.ok(gatewayPaymentService.deletePaypalConfig());
    }

    // ── ALL GATEWAYS ────────────────────────────────────────

    @GetMapping("/gateways/all")
    public ResponseEntity<ApiResponse<List<PaymentConfigResponse>>> getAllGateways() {
        return ResponseEntity.ok(gatewayPaymentService.getAllGatewayConfigs());
    }
}
