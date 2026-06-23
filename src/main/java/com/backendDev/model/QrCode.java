package com.backendDev.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Phase 5 — QR Code Entity
 * Stores the generated QR code URL and base64 image for each business.
 * Each business gets ONE QR code. Regenerating replaces the existing record.
 */
@Entity
@Table(name = "tabletop_leo_qr_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qrId;

    @Column(name = "admin_id", nullable = false, length = 100)
    private String adminId;

    @Column(name = "business_id", nullable = false, unique = true, length = 100)
    private String businessId;

    // The public menu URL encoded in the QR: http://localhost:3000/menu/{businessId}
    @Column(name = "qr_url", nullable = false, columnDefinition = "TEXT")
    private String qrUrl;

    // Base64 PNG of the generated QR image (stored for quick retrieval)
    @Column(name = "qr_image_base64", columnDefinition = "TEXT")
    private String qrImageBase64;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
