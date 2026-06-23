package com.backendDev.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for payment configuration data returned to frontend.
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
    private String environment;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
