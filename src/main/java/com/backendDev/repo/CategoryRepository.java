package com.backendDev.repo;

import com.backendDev.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByAdminIdOrderByCreatedAtDesc(String adminId);

    Optional<Category> findByCategoryIdAndAdminId(Long categoryId, String adminId);

    boolean existsByCategoryNameIgnoreCaseAndAdminId(String categoryName, String adminId);

    @Query("SELECT COUNT(c) FROM Category c WHERE c.adminId = :adminId")
    long countByAdminId(@Param("adminId") String adminId);


    // ── NEW — Phase 5 (Public Menu) ──────────────────────────────────────────
    // Used by QrCodeService to load active categories for customer menu page

    List<Category> findAllByBusinessIdAndCategoryStatus(String businessId, String categoryStatus);

    List<Category> findAllByBusinessId(String businessId);
}