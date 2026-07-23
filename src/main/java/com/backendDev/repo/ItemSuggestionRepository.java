package com.backendDev.repo;

import com.backendDev.model.ItemSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemSuggestionRepository extends JpaRepository<ItemSuggestion, Long> {

    // Case-insensitive match for businessType AND categoryName
    @Query("SELECT i FROM ItemSuggestion i WHERE LOWER(i.businessType) = LOWER(:businessType) AND LOWER(i.categoryName) = LOWER(:categoryName) ORDER BY i.displayOrder ASC")
    List<ItemSuggestion> findByBusinessTypeAndCategoryNameOrderByDisplayOrderAsc(@Param("businessType") String businessType, @Param("categoryName") String categoryName);

    @Query("SELECT i FROM ItemSuggestion i WHERE LOWER(i.businessType) = LOWER(:businessType) ORDER BY i.displayOrder ASC")
    List<ItemSuggestion> findByBusinessTypeOrderByDisplayOrderAsc(@Param("businessType") String businessType);

    void deleteByBusinessTypeAndCategoryName(String businessType, String categoryName);

    @Query("SELECT COUNT(i) > 0 FROM ItemSuggestion i WHERE LOWER(i.businessType) = LOWER(:businessType) AND LOWER(i.categoryName) = LOWER(:categoryName) AND LOWER(i.itemName) = LOWER(:itemName)")
    boolean existsByBusinessTypeAndCategoryNameAndItemName(@Param("businessType") String businessType, @Param("categoryName") String categoryName, @Param("itemName") String itemName);
}