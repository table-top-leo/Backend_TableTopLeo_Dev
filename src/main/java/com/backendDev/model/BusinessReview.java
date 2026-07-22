package com.backendDev.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Customer → Restaurant/Merchant review.
 * Not tied to a specific order — one review per (business_id, customer_phone), write-once.
 * rating is 1-5. Kept simple/flat so a future "average rating per business"
 * feature is a trivial AVG(rating) GROUP BY business_id query.
 */
@Entity
@Table(name = "tabletop_leo_business_reviews",
        uniqueConstraints = @UniqueConstraint(name = "uq_business_review_phone", columnNames = {"business_id", "customer_phone"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusinessReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_id", unique = true, nullable = false, length = 50)
    private String reviewId;

    @Column(name = "business_id", nullable = false, length = 50)
    private String businessId;

    @Column(name = "admin_id", nullable = false, length = 50)
    private String adminId;

    @Column(name = "customer_name", length = 150)
    private String customerName;

    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
