package com.backendDev.repo;

import com.backendDev.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByResetToken(String resetToken);

    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetToken p SET p.used = true, p.updatedAt = CURRENT_TIMESTAMP WHERE p.adminId = :adminId AND p.used = false")
    void invalidatePreviousTokens(@Param("adminId") String adminId);
}
