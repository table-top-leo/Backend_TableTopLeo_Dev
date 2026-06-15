package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.dto.ApiResponse;
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
        // Validate adminId exists
        userRepository.findByAdminId(request.getAdminId())
                .orElseThrow(() -> new AppException(
                    "Admin ID not found: " + request.getAdminId(),
                    HttpStatus.NOT_FOUND
                ));

        // Ensure business is not already set up for this admin
        if (businessInformationRepository.existsByAdminId(request.getAdminId())) {
            throw new AppException(
                "Business is already set up for admin ID: " + request.getAdminId(),
                HttpStatus.CONFLICT
            );
        }

        // Parse times
        LocalTime openingTime = parseTime(request.getOpeningTime(), "Opening time");
        LocalTime closingTime = parseTime(request.getClosingTime(), "Closing time");

        // Build and save business (first save to get generated id)
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

        // Generate business_id using the database-assigned id
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
