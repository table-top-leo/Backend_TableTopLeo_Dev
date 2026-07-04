package com.backendDev.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderStatusResponse {
    private String orderId;
    private String orderNumber;
    private String orderStatus;
    private String paymentStatus;
    private Integer estimatedMinutes;
    private BigDecimal grandTotal;
    private String orderType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
