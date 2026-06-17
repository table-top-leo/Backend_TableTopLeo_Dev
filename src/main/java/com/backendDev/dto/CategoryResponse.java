package com.backendDev.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private Long categoryId;
    private String adminId;
    private String businessId;
    private String categoryName;
    private String categoryImageUrl;
    private String categoryStatus;
    private long productCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}