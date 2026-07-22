package com.backendDev.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubmitBusinessReviewRequest {

    @NotBlank(message = "businessId is required")
    private String businessId;

    @NotBlank(message = "customerName is required")
    private String customerName;

    @NotBlank(message = "customerPhone is required")
    private String customerPhone;

    @NotNull(message = "rating is required")
    @Min(value = 1, message = "rating must be between 1 and 5")
    @Max(value = 5, message = "rating must be between 1 and 5")
    private Integer rating;

    private String reviewText;
}
