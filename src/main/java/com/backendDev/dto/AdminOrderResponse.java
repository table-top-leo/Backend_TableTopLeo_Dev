package com.backendDev.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminOrderResponse {

    private String orderId;
    private String orderNumber;
    private String adminId;
    private String businessId;
    private String sessionId;
    private String orderType;
    private String tableNumber;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerNote;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal grandTotal;
    private String paymentStatus;
    private String orderStatus;
    private String paymentMethod;
    private Boolean payAtCounter;
    private Integer estimatedMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AdminOrderItemResponse> items;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AdminOrderItemResponse {
        private String itemId;
        private Long productId;
        private String productName;
        private String productDescription;
        private String productImageUrl;
        private String categoryName;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal lineTotal;
        private String specialRequest;
    }
}
