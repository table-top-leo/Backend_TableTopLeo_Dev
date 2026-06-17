package com.backendDev.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Admin ID is required")
    private String adminId;

    @NotBlank(message = "Business ID is required")
    private String businessId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Item name is required")
    private String itemName;

    private String itemDescription;

    @NotNull(message = "Item price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be zero or greater")
    private BigDecimal itemPrice;

    private String itemImageUrl;

    private String productStatus;
}