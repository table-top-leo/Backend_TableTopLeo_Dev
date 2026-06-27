package com.backendDev.repo;

import com.backendDev.model.CategorySuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategorySuggestionRepository extends JpaRepository<CategorySuggestion, Long> {
    List<CategorySuggestion> findByBusinessTypeOrderByDisplayOrderAsc(String businessType);
    void deleteByBusinessType(String businessType);
    boolean existsByBusinessTypeAndCategoryName(String businessType, String categoryName);
}
