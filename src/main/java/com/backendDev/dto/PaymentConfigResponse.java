package com.backendDev.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentConfigResponse {

    private Long paymentId;
    private String adminId;
    private String businessId;
    private String paymentType;
    private String merchantName;
    private String upiId;
    private String publishableKey;
    private String paypalClientId;
    private String environment;
    private String status;

    // ── NEW ──────────────────────────────────────────────────────
    private Boolean payAtCounterEnabled;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
