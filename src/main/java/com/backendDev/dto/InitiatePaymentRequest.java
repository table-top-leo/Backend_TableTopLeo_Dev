package com.backendDev.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InitiatePaymentRequest {
    @NotBlank private String orderId;
    @NotBlank private String gatewayName;  // upi | razorpay | stripe | paypal
    private String currency;
}