package com.backendDev.repo;

import com.backendDev.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByAdminIdOrderByCreatedAtDesc(String adminId);

    List<Product> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);

    Optional<Product> findByProductIdAndAdminId(Long productId, String adminId);

    long countByCategoryId(Long categoryId);

    void deleteAllByCategoryId(Long categoryId);

    @Query("SELECT p.categoryId, COUNT(p) FROM Product p WHERE p.adminId = :adminId GROUP BY p.categoryId")
    List<Object[]> countProductsGroupedByCategoryForAdmin(@Param("adminId") String adminId);


    // ── NEW — Phase 5 (Public Menu) ──────────────────────────────────────────
    // Used by QrCodeService to load products per category for the customer menu

    List<Product> findAllByCategoryIdAndProductStatus(Long categoryId, String productStatus);

    List<Product> findAllByBusinessIdAndProductStatus(String businessId, String productStatus);
}