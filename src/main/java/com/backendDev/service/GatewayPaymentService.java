package com.backendDev.service;

import com.backendDev.dto.*;

import java.util.List;

/**
 * Service interface for Razorpay, Stripe and PayPal payment configurations.
 * UPI is handled separately in PaymentConfigurationService.
 */
public interface GatewayPaymentService {

    // ── RAZORPAY ────────────────────────────────────────────
    ApiResponse<PaymentConfigResponse> saveRazorpayConfig(RazorpayPaymentRequest request);
    ApiResponse<PaymentConfigResponse> updateRazorpayConfig(RazorpayPaymentRequest request);
    ApiResponse<PaymentConfigResponse> getRazorpayConfig();
    ApiResponse<String> deleteRazorpayConfig();

    // ── STRIPE ──────────────────────────────────────────────
    ApiResponse<PaymentConfigResponse> saveStripeConfig(StripePaymentRequest request);
    ApiResponse<PaymentConfigResponse> updateStripeConfig(StripePaymentRequest request);
    ApiResponse<PaymentConfigResponse> getStripeConfig();
    ApiResponse<String> deleteStripeConfig();

    // ── PAYPAL ──────────────────────────────────────────────
    ApiResponse<PaymentConfigResponse> savePaypalConfig(PaypalPaymentRequest request);
    ApiResponse<PaymentConfigResponse> updatePaypalConfig(PaypalPaymentRequest request);
    ApiResponse<PaymentConfigResponse> getPaypalConfig();
    ApiResponse<String> deletePaypalConfig();

    // ── ALL ─────────────────────────────────────────────────
    ApiResponse<List<PaymentConfigResponse>> getAllGatewayConfigs();
}
