package com.backendDev.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * UPDATED — Response DTO for all payment configurations.
 * Covers UPI, Razorpay, Stripe and PayPal fields.
 * Secret keys are NOT returned to frontend for security.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentConfigResponse {

    private Long paymentId;
    private String adminId;
    private String businessId;
    private String paymentType;
    private String merchantName;
    private String upiId;
    private String publishableKey;
    // ── Stripe fields ──────────────────────────
    // publishableKey also used for Stripe pk_test_xxx
    private String paypalClientId;
    private String environment;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
