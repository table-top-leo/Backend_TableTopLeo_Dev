package com.backendDev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Request DTO for saving Razorpay payment configuration.
 * admin_id and business_id come from JWT automatically.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RazorpayPaymentRequest {

    @NotBlank(message = "Razorpay Key ID is required")
    @Pattern(regexp = "^rzp_(test|live)_.+", message = "Key ID must start with rzp_test_ or rzp_live_")
    private String keyId;

    @NotBlank(message = "Razorpay Key Secret is required")
    private String keySecret;

    private String webhookSecret;

    // sandbox or live
    private String environment = "sandbox";
}
