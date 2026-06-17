package com.backendDev.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Admin ID is required")
    private String adminId;

    @NotBlank(message = "Business ID is required")
    private String businessId;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    private String categoryImageUrl;

    private String categoryStatus;
}