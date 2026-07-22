package com.backendDev.repo;

import com.backendDev.model.AppReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppReviewRepository extends JpaRepository<AppReview, Long> {

    Optional<AppReview> findByAdminId(String adminId);

    boolean existsByAdminId(String adminId);

    // Ready for the future "overall application average rating" feature.
    @Query("SELECT AVG(r.rating) FROM AppReview r")
    Double findOverallAverageRating();

    long count();
}
