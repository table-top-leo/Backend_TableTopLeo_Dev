package com.backendDev.repo;

import com.backendDev.model.BusinessInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessInformationRepository extends JpaRepository<BusinessInformation, Long> {

    Optional<BusinessInformation> findByAdminId(String adminId);

    boolean existsByAdminId(String adminId);


    // ── NEW — Phase 5 (Public Menu) ──────────────────────────────────────────
    // Used by QrCodeService to resolve business from businessId in QR URL

    Optional<BusinessInformation> findByBusinessId(String businessId);
}
