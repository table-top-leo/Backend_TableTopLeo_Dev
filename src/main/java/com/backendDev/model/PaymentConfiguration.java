package com.backendDev.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Phase 4 — Payment Configuration Entity
 * Stores UPI payment details for each admin/business.
 * admin_id and business_id are auto-resolved from JWT — never entered by admin.
 */
@Entity
@Table(name = "tabletop_leo_payment_configurations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(name = "admin_id", nullable = false, length = 100)
    private String adminId;

    @Column(name = "business_id", nullable = false, length = 100)
    private String businessId;

    @Column(name = "payment_type", nullable = false, length = 50)
    private String paymentType; // upi / razorpay / stripe / paypal

    @Column(name = "merchant_name", length = 255)
    private String merchantName;

    @Column(name = "upi_id", length = 255)
    private String upiId;

    @Column(name = "publishable_key", columnDefinition = "TEXT")
    private String publishableKey;

    @Column(name = "secret_key", columnDefinition = "TEXT")
    private String secretKey;

    @Column(name = "webhook_secret", columnDefinition = "TEXT")
    private String webhookSecret;

    @Column(name = "paypal_client_id", columnDefinition = "TEXT")
    private String paypalClientId;

    @Column(name = "environment", length = 20)
    @Builder.Default
    private String environment = "sandbox";

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "ACTIVE";
        if (environment == null) environment = "sandbox";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
