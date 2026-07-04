package com.backendDev.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlaceOrderResponse {
    private String orderId;
    private String orderNumber;
    private String sessionId;
    private String adminId;
    private String businessId;
    private String orderType;
    private String tableNumber;
    private String customerName;
    private String customerPhone;
    private String customerNote;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;
    private String paymentStatus;
    private String orderStatus;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OrderItemDto {
        private String itemId;
        private Long productId;
        private String productName;
        private String productImageUrl;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal lineTotal;
        private String specialRequest;
    }
}
