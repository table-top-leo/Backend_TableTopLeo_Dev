package com.backendDev.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemSuggestionResponse {
    private Long id;
    private String businessType;
    private String categoryName;
    private String itemName;
    private Integer displayOrder;
}
