package com.backendDev.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppReviewResponse {
    private String reviewId;
    private String adminId;
    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;
}
