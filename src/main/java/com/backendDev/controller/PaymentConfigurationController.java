package com.backendDev.controller;

import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.PaymentConfigResponse;
import com.backendDev.dto.UpiPaymentRequest;
import com.backendDev.service.PaymentConfigurationService;
import com.backendDev.serviceImpl.PaymentConfigurationServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentConfigurationController {

    private final PaymentConfigurationService paymentService;

    @PostMapping("/upi/save")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> saveUpiConfig(
            @Valid @RequestBody UpiPaymentRequest request) {
        return ResponseEntity.ok(paymentService.saveUpiConfig(request));
    }

    @PutMapping("/upi/update")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> updateUpiConfig(
            @Valid @RequestBody UpiPaymentRequest request) {
        return ResponseEntity.ok(paymentService.updateUpiConfig(request));
    }

    @GetMapping("/upi")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> getUpiConfig() {
        return ResponseEntity.ok(paymentService.getUpiConfig());
    }


    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PaymentConfigResponse>>> getAllConfigs() {
        return ResponseEntity.ok(paymentService.getAllConfigsByAdmin());
    }

    @DeleteMapping("/upi/delete")
    public ResponseEntity<ApiResponse<String>> deleteUpiConfig() {
        return ResponseEntity.ok(paymentService.deleteUpiConfig());
    }

    @PutMapping("/pay-at-counter/toggle")
    public ResponseEntity<ApiResponse<PaymentConfigResponse>> togglePayAtCounter(
            @RequestParam Boolean enabled) {
//        LOG.info("Toggling Pay at Counter: enabled={}", enabled);
        return ResponseEntity.ok(paymentService.togglePayAtCounter(enabled));
    }

    @GetMapping("/pay-at-counter/status")
    public ResponseEntity<ApiResponse<Boolean>> getPayAtCounterStatus(
            @RequestParam String businessId) {
//        LOG.info("Fetching Pay at Counter status for businessId={}", businessId);
        PaymentConfigurationServiceImpl impl = (PaymentConfigurationServiceImpl) paymentService;
        Boolean status = impl.getPayAtCounterStatus(businessId);
        return ResponseEntity.ok(ApiResponse.success("Pay at Counter status fetched", status));
    }

    @GetMapping("/pay-at-counter/my-status")
    public ResponseEntity<ApiResponse<Boolean>> getMyPayAtCounterStatus() {
        return ResponseEntity.ok(paymentService.getMyPayAtCounterStatus());
    }
}
