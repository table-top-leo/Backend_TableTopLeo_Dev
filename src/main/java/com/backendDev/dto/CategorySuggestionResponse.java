package com.backendDev.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategorySuggestionResponse {
    private Long id;
    private String businessType;
    private String categoryName;
    private String categoryEmoji;
    private Integer displayOrder;
    private String categoryImage;
    private String categoryImageType;
}
