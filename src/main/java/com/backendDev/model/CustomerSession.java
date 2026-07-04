package com.backendDev.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tabletop_leo_customer_sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", unique = true, nullable = false, length = 50)
    private String sessionId;

    @Column(name = "admin_id", nullable = false, length = 50)
    private String adminId;

    @Column(name = "business_id", nullable = false, length = 50)
    private String businessId;

    @Column(name = "order_type", length = 20)
    private String orderType;

    @Column(name = "customer_name", length = 150)
    private String customerName;

    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    @Column(name = "customer_email", length = 255)
    private String customerEmail;

    @Column(name = "table_number", length = 20)
    private String tableNumber;

    @Column(name = "session_status", nullable = false, length = 20)
    @Builder.Default
    private String sessionStatus = "ACTIVE";

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (sessionStatus == null) sessionStatus = "ACTIVE";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
