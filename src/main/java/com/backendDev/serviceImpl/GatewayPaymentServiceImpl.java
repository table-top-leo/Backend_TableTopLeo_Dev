package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.context.UserContext;
import com.backendDev.dto.*;
import com.backendDev.model.BusinessInformation;
import com.backendDev.model.PaymentConfiguration;
import com.backendDev.repo.BusinessInformationRepository;
import com.backendDev.repo.PaymentConfigurationRepository;
import com.backendDev.service.GatewayPaymentService;
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
public class GatewayPaymentServiceImpl implements GatewayPaymentService {

    private final PaymentConfigurationRepository paymentRepo;
    private final BusinessInformationRepository businessRepo;

    // ════════════════════════════════════════════════════════
    // RAZORPAY
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse<PaymentConfigResponse> saveRazorpayConfig(RazorpayPaymentRequest request) {
        String adminId = resolveAdminId();
        String businessId = resolveBusinessId(adminId);

        if (paymentRepo.existsByAdminIdAndPaymentType(adminId, "RAZORPAY")) {
            return updateRazorpayConfig(request);
        }

        PaymentConfiguration config = PaymentConfiguration.builder()
                .adminId(adminId)
                .businessId(businessId)
                .paymentType("RAZORPAY")
                .publishableKey(request.getKeyId().trim())
                .secretKey(request.getKeySecret().trim())
                .webhookSecret(request.getWebhookSecret() != null ? request.getWebhookSecret().trim() : null)
                .environment(request.getEnvironment() != null ? request.getEnvironment() : "sandbox")
                .status("ACTIVE")
                .build();

        PaymentConfiguration saved = paymentRepo.save(config);
        log.info("Razorpay config saved for admin={}, business={}", adminId, businessId);
        return ApiResponse.success("Razorpay configuration saved successfully", toResponse(saved));
    }

    @Override
    @Transactional
    public ApiResponse<PaymentConfigResponse> updateRazorpayConfig(RazorpayPaymentRequest request) {
        String adminId = resolveAdminId();

        PaymentConfiguration config = paymentRepo
                .findByAdminIdAndPaymentType(adminId, "RAZORPAY")
                .orElseThrow(() -> new AppException("No Razorpay config found.", HttpStatus.NOT_FOUND));

        config.setPublishableKey(request.getKeyId().trim());
        config.setSecretKey(request.getKeySecret().trim());
        if (request.getWebhookSecret() != null) config.setWebhookSecret(request.getWebhookSecret().trim());
        if (request.getEnvironment() != null) config.setEnvironment(request.getEnvironment());
        config.setStatus("ACTIVE");

        return ApiResponse.success("Razorpay configuration updated successfully", toResponse(paymentRepo.save(config)));
    }

    @Override
    public ApiResponse<PaymentConfigResponse> getRazorpayConfig() {
        String adminId = resolveAdminId();
        PaymentConfiguration config = paymentRepo.findByAdminIdAndPaymentType(adminId, "RAZORPAY")
                .orElseThrow(() -> new AppException("No Razorpay configuration found.", HttpStatus.NOT_FOUND));
        return ApiResponse.success("Razorpay configuration retrieved", toResponse(config));
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteRazorpayConfig() {
        String adminId = resolveAdminId();
        PaymentConfiguration config = paymentRepo.findByAdminIdAndPaymentType(adminId, "RAZORPAY")
                .orElseThrow(() -> new AppException("No Razorpay configuration found.", HttpStatus.NOT_FOUND));
        paymentRepo.delete(config);
        return ApiResponse.success("Razorpay configuration deleted successfully");
    }

    // ════════════════════════════════════════════════════════
    // STRIPE
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse<PaymentConfigResponse> saveStripeConfig(StripePaymentRequest request) {
        String adminId = resolveAdminId();
        String businessId = resolveBusinessId(adminId);

        if (paymentRepo.existsByAdminIdAndPaymentType(adminId, "STRIPE")) {
            return updateStripeConfig(request);
        }

        PaymentConfiguration config = PaymentConfiguration.builder()
                .adminId(adminId)
                .businessId(businessId)
                .paymentType("STRIPE")
                .publishableKey(request.getPublishableKey().trim())
                .secretKey(request.getSecretKey().trim())
                .webhookSecret(request.getWebhookSecret().trim())
                .environment(request.getEnvironment() != null ? request.getEnvironment() : "sandbox")
                .status("ACTIVE")
                .build();

        PaymentConfiguration saved = paymentRepo.save(config);
        log.info("Stripe config saved for admin={}, business={}", adminId, businessId);
        return ApiResponse.success("Stripe configuration saved successfully", toResponse(saved));
    }

    @Override
    @Transactional
    public ApiResponse<PaymentConfigResponse> updateStripeConfig(StripePaymentRequest request) {
        String adminId = resolveAdminId();

        PaymentConfiguration config = paymentRepo
                .findByAdminIdAndPaymentType(adminId, "STRIPE")
                .orElseThrow(() -> new AppException("No Stripe config found.", HttpStatus.NOT_FOUND));

        config.setPublishableKey(request.getPublishableKey().trim());
        config.setSecretKey(request.getSecretKey().trim());
        config.setWebhookSecret(request.getWebhookSecret().trim());
        if (request.getEnvironment() != null) config.setEnvironment(request.getEnvironment());
        config.setStatus("ACTIVE");

        return ApiResponse.success("Stripe configuration updated successfully", toResponse(paymentRepo.save(config)));
    }

    @Override
    public ApiResponse<PaymentConfigResponse> getStripeConfig() {
        String adminId = resolveAdminId();
        PaymentConfiguration config = paymentRepo.findByAdminIdAndPaymentType(adminId, "STRIPE")
                .orElseThrow(() -> new AppException("No Stripe configuration found.", HttpStatus.NOT_FOUND));
        return ApiResponse.success("Stripe configuration retrieved", toResponse(config));
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteStripeConfig() {
        String adminId = resolveAdminId();
        PaymentConfiguration config = paymentRepo.findByAdminIdAndPaymentType(adminId, "STRIPE")
                .orElseThrow(() -> new AppException("No Stripe configuration found.", HttpStatus.NOT_FOUND));
        paymentRepo.delete(config);
        return ApiResponse.success("Stripe configuration deleted successfully");
    }

    // ════════════════════════════════════════════════════════
    // PAYPAL
    // ════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ApiResponse<PaymentConfigResponse> savePaypalConfig(PaypalPaymentRequest request) {
        String adminId = resolveAdminId();
        String businessId = resolveBusinessId(adminId);

        if (paymentRepo.existsByAdminIdAndPaymentType(adminId, "PAYPAL")) {
            return updatePaypalConfig(request);
        }

        PaymentConfiguration config = PaymentConfiguration.builder()
                .adminId(adminId)
                .businessId(businessId)
                .paymentType("PAYPAL")
                .paypalClientId(request.getPaypalClientId().trim())
                .secretKey(request.getSecretKey().trim())
                .webhookSecret(request.getWebhookSecret() != null ? request.getWebhookSecret().trim() : null)
                .environment(request.getEnvironment() != null ? request.getEnvironment() : "sandbox")
                .status("ACTIVE")
                .build();

        PaymentConfiguration saved = paymentRepo.save(config);
        log.info("PayPal config saved for admin={}, business={}", adminId, businessId);
        return ApiResponse.success("PayPal configuration saved successfully", toResponse(saved));
    }

    @Override
    @Transactional
    public ApiResponse<PaymentConfigResponse> updatePaypalConfig(PaypalPaymentRequest request) {
        String adminId = resolveAdminId();

        PaymentConfiguration config = paymentRepo
                .findByAdminIdAndPaymentType(adminId, "PAYPAL")
                .orElseThrow(() -> new AppException("No PayPal config found.", HttpStatus.NOT_FOUND));

        config.setPaypalClientId(request.getPaypalClientId().trim());
        config.setSecretKey(request.getSecretKey().trim());
        if (request.getWebhookSecret() != null) config.setWebhookSecret(request.getWebhookSecret().trim());
        if (request.getEnvironment() != null) config.setEnvironment(request.getEnvironment());
        config.setStatus("ACTIVE");

        return ApiResponse.success("PayPal configuration updated successfully", toResponse(paymentRepo.save(config)));
    }

    @Override
    public ApiResponse<PaymentConfigResponse> getPaypalConfig() {
        String adminId = resolveAdminId();
        PaymentConfiguration config = paymentRepo.findByAdminIdAndPaymentType(adminId, "PAYPAL")
                .orElseThrow(() -> new AppException("No PayPal configuration found.", HttpStatus.NOT_FOUND));
        return ApiResponse.success("PayPal configuration retrieved", toResponse(config));
    }

    @Override
    @Transactional
    public ApiResponse<String> deletePaypalConfig() {
        String adminId = resolveAdminId();
        PaymentConfiguration config = paymentRepo.findByAdminIdAndPaymentType(adminId, "PAYPAL")
                .orElseThrow(() -> new AppException("No PayPal configuration found.", HttpStatus.NOT_FOUND));
        paymentRepo.delete(config);
        return ApiResponse.success("PayPal configuration deleted successfully");
    }

    // ════════════════════════════════════════════════════════
    // ALL GATEWAY CONFIGS
    // ════════════════════════════════════════════════════════

    @Override
    public ApiResponse<List<PaymentConfigResponse>> getAllGatewayConfigs() {
        String adminId = resolveAdminId();
        List<PaymentConfigResponse> list = paymentRepo.findAllByAdminId(adminId)
                .stream()
                .filter(c -> !c.getPaymentType().equals("UPI")) // UPI handled separately
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.success("Gateway configurations retrieved", list);
    }

    // ════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ════════════════════════════════════════════════════════

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
                // UPI
                .merchantName(config.getMerchantName())
                .upiId(config.getUpiId())
                // Razorpay / Stripe publishable key
                .publishableKey(config.getPublishableKey())
                // PayPal
                .paypalClientId(config.getPaypalClientId())
                // Common
                .environment(config.getEnvironment())
                .status(config.getStatus())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
