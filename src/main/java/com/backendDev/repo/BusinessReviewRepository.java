package com.backendDev.repo;

import com.backendDev.model.BusinessReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessReviewRepository extends JpaRepository<BusinessReview, Long> {

    Optional<BusinessReview> findByBusinessIdAndCustomerPhone(String businessId, String customerPhone);

    boolean existsByBusinessIdAndCustomerPhone(String businessId, String customerPhone);

    List<BusinessReview> findAllByBusinessIdOrderByCreatedAtDesc(String businessId);

    List<BusinessReview> findAllByAdminIdOrderByCreatedAtDesc(String adminId);

    // Ready for the future "average rating per business" feature.
    @Query("SELECT AVG(r.rating) FROM BusinessReview r WHERE r.businessId = :businessId")
    Double findAverageRatingByBusinessId(@Param("businessId") String businessId);

    long countByBusinessId(String businessId);
}
