package com.backendDev.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategorySuggestionRequest {
    @NotBlank(message = "Business type is required")
    private String businessType;
    @NotBlank(message = "Category name is required")
    private String categoryName;
    private String categoryEmoji;
    private Integer displayOrder;
}
