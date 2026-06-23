package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.context.UserContext;
import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.CustomerMenuResponse;
import com.backendDev.dto.QrCodeResponse;
import com.backendDev.model.*;
import com.backendDev.repo.*;
import com.backendDev.service.QrCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrCodeServiceImpl implements QrCodeService {

    private final QrCodeRepository qrCodeRepo;
    private final BusinessInformationRepository businessRepo;
    private final PaymentConfigurationRepository paymentRepo;
    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;

    // The base URL of your frontend — QR will encode: {frontendUrl}/menu/{businessId}
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    // ──────────────────────────────────────────────
    // GENERATE QR CODE (Admin triggers this)
    // ──────────────────────────────────────────────
    @Override
    @Transactional
    public ApiResponse<QrCodeResponse> generateQrCode() {

        String adminId = resolveAdminId();

        // 1. Resolve business
        BusinessInformation business = businessRepo.findByAdminId(adminId)
                .orElseThrow(() -> new AppException(
                        "Business setup not found. Complete business setup first.", HttpStatus.BAD_REQUEST
                ));
        String businessId = business.getBusinessId();

        // 2. Validate that payment config exists (prerequisite per Phase 5 spec)
        List<PaymentConfiguration> paymentConfigs = paymentRepo.findAllByAdminId(adminId);
        if (paymentConfigs.isEmpty()) {
            throw new AppException(
                    "Please configure at least one payment method before generating a QR code.",
                    HttpStatus.BAD_REQUEST
            );
        }

        // 3. Build the public menu URL
        String menuUrl = frontendUrl + "/menu/" + businessId;

        // 4. Generate QR code image as base64 PNG using ZXing
        String qrBase64 = generateQrBase64(menuUrl);

        // 5. Upsert: if QR already exists for this business, update it
        QrCode qrCode = qrCodeRepo.findByBusinessId(businessId)
                .map(existing -> {
                    existing.setQrUrl(menuUrl);
                    existing.setQrImageBase64(qrBase64);
                    existing.setStatus("ACTIVE");
                    return existing;
                })
                .orElse(QrCode.builder()
                        .adminId(adminId)
                        .businessId(businessId)
                        .qrUrl(menuUrl)
                        .qrImageBase64(qrBase64)
                        .status("ACTIVE")
                        .build());

        QrCode saved = qrCodeRepo.save(qrCode);
        log.info("QR code generated/updated for business={}", businessId);

        return ApiResponse.success("QR code generated successfully", toResponse(saved));
    }

    // ──────────────────────────────────────────────
    // GET EXISTING QR CODE (for dashboard display)
    // ──────────────────────────────────────────────
    @Override
    public ApiResponse<QrCodeResponse> getQrCode() {
        String adminId = resolveAdminId();

        QrCode qrCode = qrCodeRepo.findByAdminId(adminId)
                .orElseThrow(() -> new AppException(
                        "No QR code found. Please generate one first.", HttpStatus.NOT_FOUND
                ));

        return ApiResponse.success("QR code retrieved successfully", toResponse(qrCode));
    }

    // ──────────────────────────────────────────────
    // PUBLIC MENU (No auth — anyone who scans QR can call this)
    // ──────────────────────────────────────────────
    @Override
    public ApiResponse<CustomerMenuResponse> getPublicMenu(String businessId) {

        // 1. Load business info
        BusinessInformation business = businessRepo.findByBusinessId(businessId)
                .orElseThrow(() -> new AppException(
                        "Business not found. Invalid QR code.", HttpStatus.NOT_FOUND
                ));

        // 2. Load active categories for this business
        List<Category> categories = categoryRepo
                .findAllByBusinessIdAndCategoryStatus(businessId, "ACTIVE");

        // 3. For each category, load active products
        List<CustomerMenuResponse.CategoryWithProducts> catWithProducts = categories.stream()
                .map(cat -> {
                    List<Product> products = productRepo
                            .findAllByCategoryIdAndProductStatus(cat.getCategoryId(), "ACTIVE");

                    List<CustomerMenuResponse.ProductItem> productItems = products.stream()
                            .map(p -> CustomerMenuResponse.ProductItem.builder()
                                    .productId(p.getProductId())
                                    .itemName(p.getItemName())
                                    .itemDescription(p.getItemDescription())
                                    .itemPrice(p.getItemPrice())
                                    .itemImageUrl(p.getItemImageUrl())
                                    .productStatus(p.getProductStatus())
                                    .build())
                            .collect(Collectors.toList());

                    return CustomerMenuResponse.CategoryWithProducts.builder()
                            .categoryId(cat.getCategoryId())
                            .categoryName(cat.getCategoryName())
                            .categoryImageUrl(cat.getCategoryImageUrl())
                            .categoryStatus(cat.getCategoryStatus())
                            .products(productItems)
                            .build();
                })
                .collect(Collectors.toList());

        // 4. Build business info for response
        CustomerMenuResponse.BusinessInfo businessInfo = CustomerMenuResponse.BusinessInfo.builder()
                .businessId(business.getBusinessId())
                .businessName(business.getBusinessName())
                .businessType(business.getBusinessType())
                .businessEmail(business.getBusinessEmail())
                .businessPhone(business.getBusinessPhone())
                .logoUrl(business.getLogoUrl())
                .businessDescription(business.getBusinessDescription())
                .addressLine1(business.getAddressLine1())
                .city(business.getCity())
                .state(business.getState())
                .country(business.getCountry())
                .openingTime(business.getOpeningTime() != null ? business.getOpeningTime().toString() : null)
                .closingTime(business.getClosingTime() != null ? business.getClosingTime().toString() : null)
                .workingDays(business.getWorkingDays())
                .status("ACTIVE")
                .build();

        CustomerMenuResponse menu = CustomerMenuResponse.builder()
                .business(businessInfo)
                .categories(catWithProducts)
                .build();

        return ApiResponse.success("Menu loaded successfully", menu);
    }

    // ──────────────────────────────────────────────
    // PRIVATE HELPERS
    // ──────────────────────────────────────────────

    private String resolveAdminId() {
        String adminId = UserContext.getAdminId();
        if (adminId == null || adminId.isBlank()) {
            throw new AppException("Authentication required.", HttpStatus.UNAUTHORIZED);
        }
        return adminId;
    }

    /**
     * Uses ZXing to generate a QR code for the given URL.
     * Returns "data:image/png;base64,..." string ready to use in <img src="...">
     */
    private String generateQrBase64(String url) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2);

            BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, 400, 400, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
            byte[] pngBytes = outputStream.toByteArray();

            String base64 = Base64.getEncoder().encodeToString(pngBytes);
            return "data:image/png;base64," + base64;

        } catch (Exception e) {
            log.error("Failed to generate QR code for URL: {}", url, e);
            throw new AppException("Failed to generate QR code: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private QrCodeResponse toResponse(QrCode qrCode) {
        return QrCodeResponse.builder()
                .qrId(qrCode.getQrId())
                .adminId(qrCode.getAdminId())
                .businessId(qrCode.getBusinessId())
                .qrUrl(qrCode.getQrUrl())
                .qrImageBase64(qrCode.getQrImageBase64())
                .status(qrCode.getStatus())
                .createdAt(qrCode.getCreatedAt())
                .updatedAt(qrCode.getUpdatedAt())
                .build();
    }
}
