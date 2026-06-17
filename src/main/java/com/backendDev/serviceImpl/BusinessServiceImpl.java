package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.BusinessInformationResponse;
import com.backendDev.dto.BusinessSetupRequest;
import com.backendDev.dto.BusinessSetupResponse;
import com.backendDev.model.BusinessInformation;
import com.backendDev.repo.BusinessInformationRepository;
import com.backendDev.repo.UserRepository;
import com.backendDev.service.BusinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessServiceImpl implements BusinessService {

    private final BusinessInformationRepository businessInformationRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    @Transactional
    public ApiResponse<BusinessSetupResponse> setupBusiness(BusinessSetupRequest request) {
        userRepository.findByAdminId(request.getAdminId())
                .orElseThrow(() -> new AppException(
                    "Admin ID not found: " + request.getAdminId(),
                    HttpStatus.NOT_FOUND
                ));

        if (businessInformationRepository.existsByAdminId(request.getAdminId())) {
            throw new AppException(
                "Business is already set up for admin ID: " + request.getAdminId(),
                HttpStatus.CONFLICT
            );
        }

        LocalTime openingTime = parseTime(request.getOpeningTime(), "Opening time");
        LocalTime closingTime = parseTime(request.getClosingTime(), "Closing time");

        BusinessInformation business = BusinessInformation.builder()
                .adminId(request.getAdminId())
                .businessType(request.getBusinessType())
                .businessName(request.getBusinessName())
                .businessEmail(request.getBusinessEmail())
                .businessPhone(request.getBusinessPhone())
                .logoUrl(nullIfBlank(request.getLogoUrl()))
                .gstNumber(nullIfBlank(request.getGstNumber()))
                .licenseNumber(nullIfBlank(request.getLicenseNumber()))
                .website(nullIfBlank(request.getWebsite()))
                .addressLine1(request.getAddressLine1())
                .addressLine2(nullIfBlank(request.getAddressLine2()))
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .openingTime(openingTime)
                .closingTime(closingTime)
                .workingDays(request.getWorkingDays())
                .timezone(request.getTimezone())
                .businessDescription(nullIfBlank(request.getBusinessDescription()))
                .setupCompleted(false)
                .build();

        BusinessInformation saved = businessInformationRepository.save(business);

        String businessId = String.format("BUS%06d", saved.getId());
        saved.setBusinessId(businessId);
        saved.setSetupCompleted(true);
        businessInformationRepository.save(saved);

        log.info("Business setup completed: {} for admin: {}", businessId, request.getAdminId());

        BusinessSetupResponse data = BusinessSetupResponse.builder()
                .businessId(businessId)
                .adminId(request.getAdminId())
                .businessName(saved.getBusinessName())
                .businessType(saved.getBusinessType())
                .city(saved.getCity())
                .state(saved.getState())
                .country(saved.getCountry())
                .message("Business setup completed successfully.")
                .build();

        return ApiResponse.success("Business information saved successfully.", data);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<BusinessInformationResponse> getBusinessInformation(String adminId) {
        BusinessInformation business = businessInformationRepository.findByAdminId(adminId)
                .orElseThrow(() -> new AppException(
                    "Business information not found for admin ID: " + adminId,
                    HttpStatus.NOT_FOUND
                ));

        log.info("Business information fetched for admin: {}", adminId);
        return ApiResponse.success("Business information fetched successfully", mapToResponse(business));
    }

    private BusinessInformationResponse mapToResponse(BusinessInformation business) {
        return BusinessInformationResponse.builder()
                .businessId(business.getBusinessId())
                .adminId(business.getAdminId())
                .businessType(business.getBusinessType())
                .businessName(business.getBusinessName())
                .businessEmail(business.getBusinessEmail())
                .businessPhone(business.getBusinessPhone())
                .logoUrl(business.getLogoUrl())
                .gstNumber(business.getGstNumber())
                .licenseNumber(business.getLicenseNumber())
                .website(business.getWebsite())
                .addressLine1(business.getAddressLine1())
                .addressLine2(business.getAddressLine2())
                .city(business.getCity())
                .state(business.getState())
                .country(business.getCountry())
                .postalCode(business.getPostalCode())
                .openingTime(business.getOpeningTime())
                .closingTime(business.getClosingTime())
                .workingDays(business.getWorkingDays())
                .timezone(business.getTimezone())
                .businessDescription(business.getBusinessDescription())
                .setupCompleted(business.getSetupCompleted())
                .createdAt(business.getCreatedAt())
                .updatedAt(business.getUpdatedAt())
                .build();
    }

    private LocalTime parseTime(String timeStr, String fieldName) {
        try {
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new AppException(
                fieldName + " is invalid. Expected format: HH:mm (e.g. 09:00)",
                HttpStatus.BAD_REQUEST
            );
        }
    }

    private String nullIfBlank(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
