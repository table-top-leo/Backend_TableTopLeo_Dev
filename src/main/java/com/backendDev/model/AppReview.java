package com.backendDev.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Restaurant/Merchant (admin) → Application review.
 * One review per admin_id, write-once. rating is 1-5.
 * Flat structure so a future "overall app average rating" is a trivial
 * AVG(rating) query across the whole table.
 */
@Entity
@Table(name = "tabletop_leo_app_reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "review_id", unique = true, nullable = false, length = 50)
    private String reviewId;

    @Column(name = "admin_id", unique = true, nullable = false, length = 50)
    private String adminId;

    @Column(name = "business_id", length = 50)
    private String businessId;

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
