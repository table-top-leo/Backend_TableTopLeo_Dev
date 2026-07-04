package com.backendDev.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConfirmPaymentResponse {
    private String paymentId;
    private String orderId;
    private String orderNumber;
    private String orderStatus;
    private String paymentStatus;
    private BigDecimal grandTotal;
    private String gatewayName;
    private String businessName;
    private String orderType;
    private String customerName;
    private Integer estimatedMinutes;
    private LocalDateTime createdAt;
}