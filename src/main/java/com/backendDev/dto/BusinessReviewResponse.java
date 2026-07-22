package com.backendDev.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusinessReviewResponse {
    private String reviewId;
    private String businessId;
    private String customerName;
    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;
}
