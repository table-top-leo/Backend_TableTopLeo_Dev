package com.backendDev.repo;

import com.backendDev.model.ItemSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemSuggestionRepository extends JpaRepository<ItemSuggestion, Long> {
    List<ItemSuggestion> findByBusinessTypeAndCategoryNameOrderByDisplayOrderAsc(String businessType, String categoryName);
    List<ItemSuggestion> findByBusinessTypeOrderByDisplayOrderAsc(String businessType);
    void deleteByBusinessTypeAndCategoryName(String businessType, String categoryName);
    boolean existsByBusinessTypeAndCategoryNameAndItemName(String businessType, String categoryName, String itemName);
}
