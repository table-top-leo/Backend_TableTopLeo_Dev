package com.backendDev.repo;

import com.backendDev.model.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, Long> {

    Optional<QrCode> findByBusinessId(String businessId);

    Optional<QrCode> findByAdminId(String adminId);

    boolean existsByBusinessId(String businessId);
}
