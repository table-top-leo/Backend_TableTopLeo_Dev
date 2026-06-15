package com.backendDev.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tabletop_leo_business_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_id", unique = true, nullable = false, length = 50)
    private String businessId;

    @Column(name = "admin_id", length = 50)
    private String adminId;

    @Column(name = "business_type", nullable = false, length = 100)
    private String businessType;

    @Column(name = "business_name", nullable = false, length = 255)
    private String businessName;

    @Column(name = "business_email", nullable = false, length = 255)
    private String businessEmail;

    @Column(name = "business_phone", nullable = false, length = 20)
    private String businessPhone;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Column(name = "gst_number", length = 50)
    private String gstNumber;

    @Column(name = "license_number", length = 100)
    private String licenseNumber;

    @Column(name = "website", length = 255)
    private String website;

    @Column(name = "address_line1", nullable = false, length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Column(name = "working_days", nullable = false, length = 255)
    private String workingDays;

    @Column(name = "timezone", nullable = false, length = 100)
    private String timezone;

    @Column(name = "business_description", columnDefinition = "TEXT")
    private String businessDescription;

    @Column(name = "setup_completed")
    @Builder.Default
    private Boolean setupCompleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (setupCompleted == null) setupCompleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
