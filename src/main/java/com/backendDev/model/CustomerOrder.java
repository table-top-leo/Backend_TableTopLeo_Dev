package com.backendDev.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tabletop_leo_orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", unique = true, nullable = false, length = 50)
    private String orderId;

    @Column(name = "order_number", nullable = false, length = 30)
    private String orderNumber;

    @Column(name = "session_id", nullable = false, length = 50)
    private String sessionId;

    @Column(name = "admin_id", nullable = false, length = 50)
    private String adminId;

    @Column(name = "business_id", nullable = false, length = 50)
    private String businessId;

    @Column(name = "order_type", nullable = false, length = 20)
    private String orderType;  // DINE_IN | TAKE_AWAY

    @Column(name = "table_number", length = 20)
    private String tableNumber;

    @Column(name = "customer_name", length = 150)
    private String customerName;

    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    @Column(name = "customer_email", length = 255)
    private String customerEmail;

    @Column(name = "customer_note", columnDefinition = "TEXT")
    private String customerNote;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "grand_total", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal grandTotal = BigDecimal.ZERO;

    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private String paymentStatus = "PENDING";  // PENDING | PAID | FAILED

    @Column(name = "order_status", nullable = false, length = 20)
    @Builder.Default
    private String orderStatus = "PLACED";  // PLACED | ACCEPTED | PREPARING | READY | COMPLETED | CANCELLED

    @Column(name = "payment_method", length = 30)
    private String paymentMethod;  // upi | razorpay | stripe | paypal

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (paymentStatus == null) paymentStatus = "PENDING";
        if (orderStatus   == null) orderStatus   = "PLACED";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
