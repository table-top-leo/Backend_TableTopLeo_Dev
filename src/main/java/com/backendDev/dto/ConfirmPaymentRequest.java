package com.backendDev.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConfirmPaymentRequest {
    @NotBlank private String paymentId;
    @NotBlank private String orderId;
    @NotBlank private String gatewayName;
    private String transactionId;
    private String paymentReference;
    private String razorpaySignature;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String stripePaymentIntentId;
    private String paypalOrderId;
    private String paypalCaptureId;
    private String gatewayResponse;
}