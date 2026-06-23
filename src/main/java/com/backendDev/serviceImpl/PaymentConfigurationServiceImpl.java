package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.context.UserContext;
import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.PaymentConfigResponse;
import com.backendDev.dto.UpiPaymentRequest;
import com.backendDev.model.BusinessInformation;
import com.backendDev.model.PaymentConfiguration;
import com.backendDev.repo.BusinessInformationRepository;
import com.backendDev.repo.PaymentConfigurationRepository;
import com.backendDev.service.PaymentConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConfigurationServiceImpl implements PaymentConfigurationService {

    private final PaymentConfigurationRepository paymentRepo;
    private final BusinessInformationRepository businessRepo;

    // ──────────────────────────────────────────────
    // SAVE UPI CONFIG (Create or replace)
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse<PaymentConfigResponse> saveUpiConfig(UpiPaymentRequest request) {

        // 1. Get adminId from JWT (via ThreadLocal UserContext — set by JwtFilter)
        String adminId = resolveAdminId();

        // 2. Get businessId from the admin's business record (auto — no manual input needed)
        String businessId = resolveBusinessId(adminId);

        // 3. If UPI config already exists for this admin, update it instead
        if (paymentRepo.existsByAdminIdAndPaymentType(adminId, "UPI")) {
            return updateUpiConfig(request);
        }

        // 4. Build and save new record
        PaymentConfiguration config = PaymentConfiguration.builder()
                .adminId(adminId)
                .businessId(businessId)
                .paymentType("UPI")
                .merchantName(request.getMerchantName().trim())
                .upiId(request.getUpiId().trim().toLowerCase())
                .environment("live")
                .status("ACTIVE")
                .build();

        PaymentConfiguration saved = paymentRepo.save(config);
        log.info("UPI config saved for admin={}, business={}", adminId, businessId);

        return ApiResponse.success("UPI payment configuration saved successfully", toResponse(saved));
    }

    // ──────────────────────────────────────────────
    // UPDATE UPI CONFIG
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse<PaymentConfigResponse> updateUpiConfig(UpiPaymentRequest request) {

        String adminId = resolveAdminId();

        PaymentConfiguration config = paymentRepo
                .findByAdminIdAndPaymentType(adminId, "UPI")
                .orElseThrow(() -> new AppException(
                        "No UPI configuration found. Please save first.", HttpStatus.NOT_FOUND
                ));

        config.setMerchantName(request.getMerchantName().trim());
        config.setUpiId(request.getUpiId().trim().toLowerCase());
        config.setStatus("ACTIVE");

        PaymentConfiguration updated = paymentRepo.save(config);
        log.info("UPI config updated for admin={}", adminId);

        return ApiResponse.success("UPI payment configuration updated successfully", toResponse(updated));
    }

    // ──────────────────────────────────────────────
    // GET UPI CONFIG
    // ──────────────────────────────────────────────
    @Override
    public ApiResponse<PaymentConfigResponse> getUpiConfig() {
        String adminId = resolveAdminId();

        PaymentConfiguration config = paymentRepo
                .findByAdminIdAndPaymentType(adminId, "UPI")
                .orElseThrow(() -> new AppException(
                        "No UPI configuration found for this account.", HttpStatus.NOT_FOUND
                ));

        return ApiResponse.success("UPI configuration retrieved successfully", toResponse(config));
    }

    // ──────────────────────────────────────────────
    // GET ALL CONFIGS FOR ADMIN
    // ──────────────────────────────────────────────
    @Override
    public ApiResponse<List<PaymentConfigResponse>> getAllConfigsByAdmin() {
        String adminId = resolveAdminId();

        List<PaymentConfigResponse> list = paymentRepo
                .findAllByAdminId(adminId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.success("Payment configurations retrieved", list);
    }

    // ──────────────────────────────────────────────
    // DELETE UPI CONFIG
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse<String> deleteUpiConfig() {
        String adminId = resolveAdminId();

        PaymentConfiguration config = paymentRepo
                .findByAdminIdAndPaymentType(adminId, "UPI")
                .orElseThrow(() -> new AppException(
                        "No UPI configuration found.", HttpStatus.NOT_FOUND
                ));

        paymentRepo.delete(config);
        log.info("UPI config deleted for admin={}", adminId);

        return ApiResponse.success("UPI configuration deleted successfully");
    }

    // ──────────────────────────────────────────────
    // PRIVATE HELPERS
    // ──────────────────────────────────────────────

    private String resolveAdminId() {
        String adminId = UserContext.getAdminId();
        if (adminId == null || adminId.isBlank()) {
            throw new AppException("Authentication required. Please log in.", HttpStatus.UNAUTHORIZED);
        }
        return adminId;
    }

    private String resolveBusinessId(String adminId) {
        BusinessInformation business = businessRepo.findByAdminId(adminId)
                .orElseThrow(() -> new AppException(
                        "Business setup not completed. Please set up your business first.",
                        HttpStatus.BAD_REQUEST
                ));
        return business.getBusinessId();
    }

    private PaymentConfigResponse toResponse(PaymentConfiguration config) {
        return PaymentConfigResponse.builder()
                .paymentId(config.getPaymentId())
                .adminId(config.getAdminId())
                .businessId(config.getBusinessId())
                .paymentType(config.getPaymentType())
                .merchantName(config.getMerchantName())
                .upiId(config.getUpiId())
                .environment(config.getEnvironment())
                .status(config.getStatus())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
