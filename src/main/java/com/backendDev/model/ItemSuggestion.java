package com.backendDev.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tabletop_leo_item_suggestions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_type", nullable = false)
    private String businessType;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "display_order")
    private Integer displayOrder;
}
