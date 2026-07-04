package com.backendDev.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tabletop_leo_order_payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", unique = true, nullable = false, length = 50)
    private String paymentId;

    @Column(name = "order_id", nullable = false, length = 50)
    private String orderId;

    @Column(name = "admin_id", nullable = false, length = 50)
    private String adminId;

    @Column(name = "business_id", nullable = false, length = 50)
    private String businessId;

    @Column(name = "gateway_name", nullable = false, length = 30)
    private String gatewayName;  // upi | razorpay | stripe | paypal

    @Column(name = "transaction_id", length = 300)
    private String transactionId;

    @Column(name = "payment_reference", length = 300)
    private String paymentReference;

    @Column(name = "paid_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "currency", nullable = false, length = 10)
    @Builder.Default
    private String currency = "INR";

    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private String paymentStatus = "PENDING";

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "initiated_at")
    private LocalDateTime initiatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt    = LocalDateTime.now();
        updatedAt    = LocalDateTime.now();
        initiatedAt  = LocalDateTime.now();
        if (paymentStatus == null) paymentStatus = "PENDING";
        if (currency      == null) currency      = "INR";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
