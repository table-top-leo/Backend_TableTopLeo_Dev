package com.backendDev.repo;

import com.backendDev.model.PaymentConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentConfigurationRepository extends JpaRepository<PaymentConfiguration, Long> {

    Optional<PaymentConfiguration> findByAdminIdAndPaymentType(String adminId, String paymentType);

    Optional<PaymentConfiguration> findByBusinessIdAndPaymentType(String businessId, String paymentType);

    List<PaymentConfiguration> findAllByAdminId(String adminId);

    List<PaymentConfiguration> findAllByBusinessId(String businessId);

    boolean existsByAdminIdAndPaymentType(String adminId, String paymentType);
}
