package com.backendDev.service;

import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.PaymentConfigResponse;
import com.backendDev.dto.UpiPaymentRequest;

import java.util.List;

public interface PaymentConfigurationService {

    ApiResponse<PaymentConfigResponse> saveUpiConfig(UpiPaymentRequest request);

    ApiResponse<PaymentConfigResponse> updateUpiConfig(UpiPaymentRequest request);

    ApiResponse<PaymentConfigResponse> getUpiConfig();

    ApiResponse<List<PaymentConfigResponse>> getAllConfigsByAdmin();

    ApiResponse<String> deleteUpiConfig();

    // ── NEW: Pay At Counter toggle ────────────────────────────────
    ApiResponse<PaymentConfigResponse> togglePayAtCounter(Boolean enabled);

    // GET current admin's saved Pay at Counter status (for admin page load)
    ApiResponse<Boolean> getMyPayAtCounterStatus();
}
