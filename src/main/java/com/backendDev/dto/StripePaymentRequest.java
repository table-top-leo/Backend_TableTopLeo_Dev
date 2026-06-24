package com.backendDev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Request DTO for saving Stripe payment configuration.
 * admin_id and business_id come from JWT automatically.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StripePaymentRequest {

    @NotBlank(message = "Stripe Publishable Key is required")
    @Pattern(regexp = "^pk_(test|live)_.+", message = "Publishable key must start with pk_test_ or pk_live_")
    private String publishableKey;

    @NotBlank(message = "Stripe Secret Key is required")
    @Pattern(regexp = "^sk_(test|live)_.+", message = "Secret key must start with sk_test_ or sk_live_")
    private String secretKey;

    @NotBlank(message = "Webhook Signing Secret is required")
    private String webhookSecret;

    // sandbox or live
    private String environment = "sandbox";
}
