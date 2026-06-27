package com.backendDev.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemSuggestionRequest {
    @NotBlank(message = "Business type is required")
    private String businessType;
    @NotBlank(message = "Category name is required")
    private String categoryName;
    @NotBlank(message = "Item name is required")
    private String itemName;
    private Integer displayOrder;
}
