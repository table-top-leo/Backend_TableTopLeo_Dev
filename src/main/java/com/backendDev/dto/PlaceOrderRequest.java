package com.backendDev.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlaceOrderRequest {

    @NotBlank(message = "sessionId is required")
    private String sessionId;

    @NotBlank(message = "businessId is required")
    private String businessId;

    @NotBlank(message = "orderType is required")
    private String orderType;  // DINE_IN | TAKE_AWAY

    private String tableNumber;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerNote;

    @NotEmpty(message = "Cart cannot be empty")
    private List<CartItemDto> items;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CartItemDto {
        @NotNull private Long productId;
        @NotBlank private String productName;
        private String productDescription;
        private String productImageUrl;
        private String categoryName;
        @NotNull private BigDecimal unitPrice;
        @NotNull @Min(1) private Integer quantity;
        private String specialRequest;
    }
}