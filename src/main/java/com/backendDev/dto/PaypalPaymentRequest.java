package com.backendDev.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request DTO for saving PayPal payment configuration.
 * admin_id and business_id come from JWT automatically.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaypalPaymentRequest {

    @NotBlank(message = "PayPal Client ID is required")
    private String paypalClientId;

    @NotBlank(message = "PayPal Secret Key is required")
    private String secretKey;

    private String webhookSecret;

    // sandbox or live
    private String environment = "sandbox";
}
