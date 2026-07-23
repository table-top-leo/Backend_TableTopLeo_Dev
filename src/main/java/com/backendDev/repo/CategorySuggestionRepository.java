package com.backendDev.repo;

import com.backendDev.model.CategorySuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategorySuggestionRepository extends JpaRepository<CategorySuggestion, Long> {

    // Case-insensitive match — fixes "Restaurant" vs "restaurant" mismatch
    @Query("SELECT c FROM CategorySuggestion c WHERE LOWER(c.businessType) = LOWER(:businessType) ORDER BY c.displayOrder ASC")
    List<CategorySuggestion> findByBusinessTypeOrderByDisplayOrderAsc(@Param("businessType") String businessType);

    void deleteByBusinessType(String businessType);

    @Query("SELECT COUNT(c) > 0 FROM CategorySuggestion c WHERE LOWER(c.businessType) = LOWER(:businessType) AND LOWER(c.categoryName) = LOWER(:categoryName)")
    boolean existsByBusinessTypeAndCategoryName(@Param("businessType") String businessType, @Param("categoryName") String categoryName);
}