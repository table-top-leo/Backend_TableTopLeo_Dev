package com.backendDev.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tabletop_leo_payment_configurations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(name = "admin_id", nullable = false, length = 100)
    private String adminId;

    @Column(name = "business_id", nullable = false, length = 100)
    private String businessId;

    @Column(name = "payment_type", nullable = false, length = 50)
    private String paymentType;

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

    // ── NEW: Pay At Counter toggle — admin enables/disables from Payment Setup ──
    @Column(name = "pay_at_counter_enabled", nullable = false)
    @Builder.Default
    private Boolean payAtCounterEnabled = false;

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
        if (payAtCounterEnabled == null) payAtCounterEnabled = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
