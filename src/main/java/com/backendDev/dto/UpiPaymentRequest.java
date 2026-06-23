package com.backendDev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * Request DTO for saving UPI payment configuration.
 * admin_id and business_id are NOT included here — they come from JWT automatically.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpiPaymentRequest {

    @NotBlank(message = "Merchant name is required")
    private String merchantName;

    @NotBlank(message = "UPI ID is required")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$",
        message = "Invalid UPI ID format. Example: name@oksbi"
    )
    private String upiId;
}
