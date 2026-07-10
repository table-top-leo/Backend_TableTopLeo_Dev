package com.backendDev.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WebSocketEvent {

    // Event type — determines what the frontend does with this message
    // NEW_ORDER | STATUS_UPDATED | ORDER_CANCELLED
    private String eventType;

    // Order details
    private String orderId;
    private String orderNumber;
    private String adminId;
    private String businessId;
    private String sessionId;

    // For admin dashboard bell notification
    private String orderType;       // DINE_IN | TAKE_AWAY
    private String tableNumber;
    private String customerName;
    private String customerPhone;
    private BigDecimal grandTotal;
    private String paymentStatus;
    private String paymentMethod;
    private Boolean payAtCounter;

    // Status update (for customer tracking)
    private String orderStatus;
    private String previousStatus;
    private String statusMessage;   // Human-readable e.g. "Your order is being prepared"
    private Integer estimatedMinutes;

    private LocalDateTime timestamp;
}
