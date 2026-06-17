package com.backendDev.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long productId;
    private String adminId;
    private String businessId;
    private Long categoryId;
    private String categoryName;
    private String itemName;
    private String itemDescription;
    private BigDecimal itemPrice;
    private String itemImageUrl;
    private String productStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}