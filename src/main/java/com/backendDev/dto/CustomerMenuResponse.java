package com.backendDev.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Full menu response for the public /menu/{businessId} customer page.
 * This endpoint requires NO authentication — any customer scanning the QR can access it.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerMenuResponse {

    private BusinessInfo business;
    private List<CategoryWithProducts> categories;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BusinessInfo {
        private String businessId;
        private String businessName;
        private String businessType;
        private String businessEmail;
        private String businessPhone;
        private String logoUrl;
        private String businessDescription;
        private String addressLine1;
        private String city;
        private String state;
        private String country;
        private String openingTime;
        private String closingTime;
        private String workingDays;
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryWithProducts {
        private Long categoryId;
        private String categoryName;
        private String categoryImageUrl;
        private String categoryStatus;
        private List<ProductItem> products;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductItem {
        private Long productId;
        private String itemName;
        private String itemDescription;
        private BigDecimal itemPrice;
        private String itemImageUrl;
        private String productStatus;
    }
}
