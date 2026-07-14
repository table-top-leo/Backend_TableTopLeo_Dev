package com.backendDev.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tabletop_leo_category_suggestions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CategorySuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_type", nullable = false)
    private String businessType;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "category_emoji")
    private String categoryEmoji;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "category_image", length = 500)
    private String categoryImage;

    @Column(name = "category_image_type", length = 50)
    private String categoryImageType;
}
