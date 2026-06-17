package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.dto.*;
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

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

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

        long nextNumber = businessInformationRepository.count() + 1;
        String businessId = String.format("BUS%06d", nextNumber);

        BusinessInformation business = BusinessInformation.builder()
                .businessId(businessId)
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
                .setupCompleted(true)
                .build();

        BusinessInformation saved = businessInformationRepository.save(business);
        log.info("Business setup completed: {} for admin: {}", businessId, request.getAdminId());

        BusinessSetupResponse response = BusinessSetupResponse.builder()
                .businessId(saved.getBusinessId())
                .adminId(saved.getAdminId())
                .businessName(saved.getBusinessName())
                .businessType(saved.getBusinessType())
                .city(saved.getCity())
                .state(saved.getState())
                .country(saved.getCountry())
                .message("Business setup completed successfully.")
                .build();

        return ApiResponse.success("Business information saved successfully.", response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<BusinessInformationResponse> getBusinessInformation(String adminId) {
        log.info("Fetching business information for admin ID: [{}]", adminId);

        BusinessInformation business = businessInformationRepository.findByAdminId(adminId)
                .orElseThrow(() -> new AppException(
                        "Business information not found for admin ID: " + adminId,
                        HttpStatus.NOT_FOUND
                ));

        log.info("Business information fetched for admin: {}", adminId);
        return ApiResponse.success("Business information fetched successfully", mapToResponse(business));
    }

    @Override
    @Transactional
    public ApiResponse<BusinessUpdateResponse> updateBusinessInformation(String adminId, BusinessUpdateRequest request) {
        log.info("Updating business information for admin ID: [{}]", adminId);

        BusinessInformation business = businessInformationRepository.findByAdminId(adminId)
                .orElseThrow(() -> new AppException(
                        "Business information not found for admin ID: " + adminId,
                        HttpStatus.NOT_FOUND
                ));

        LocalTime openingTime = parseTime(request.getOpeningTime(), "Opening time");
        LocalTime closingTime = parseTime(request.getClosingTime(), "Closing time");

        business.setBusinessName(request.getBusinessName());
        business.setBusinessEmail(request.getBusinessEmail());
        business.setBusinessPhone(request.getBusinessPhone());
        business.setLogoUrl(nullIfBlank(request.getLogoUrl()));
        business.setGstNumber(nullIfBlank(request.getGstNumber()));
        business.setLicenseNumber(nullIfBlank(request.getLicenseNumber()));
        business.setWebsite(nullIfBlank(request.getWebsite()));
        business.setAddressLine1(request.getAddressLine1());
        business.setAddressLine2(nullIfBlank(request.getAddressLine2()));
        business.setCity(request.getCity());
        business.setState(request.getState());
        business.setCountry(request.getCountry());
        business.setPostalCode(request.getPostalCode());
        business.setOpeningTime(openingTime);
        business.setClosingTime(closingTime);
        business.setWorkingDays(nullIfBlank(request.getWorkingDays()));
        business.setTimezone(request.getTimezone());
        business.setBusinessDescription(nullIfBlank(request.getBusinessDescription()));

        BusinessInformation updated = businessInformationRepository.save(business);
        log.info("Business information updated for admin: {}", adminId);

        BusinessUpdateResponse response = BusinessUpdateResponse.builder()
                .businessId(updated.getBusinessId())
                .adminId(updated.getAdminId())
                .businessName(updated.getBusinessName())
                .businessType(updated.getBusinessType())
                .businessEmail(updated.getBusinessEmail())
                .businessPhone(updated.getBusinessPhone())
                .logoUrl(updated.getLogoUrl())
                .gstNumber(updated.getGstNumber())
                .licenseNumber(updated.getLicenseNumber())
                .website(updated.getWebsite())
                .addressLine1(updated.getAddressLine1())
                .addressLine2(updated.getAddressLine2())
                .city(updated.getCity())
                .state(updated.getState())
                .country(updated.getCountry())
                .postalCode(updated.getPostalCode())
                .openingTime(updated.getOpeningTime())
                .closingTime(updated.getClosingTime())
                .workingDays(updated.getWorkingDays())
                .timezone(updated.getTimezone())
                .businessDescription(updated.getBusinessDescription())
                .setupCompleted(updated.getSetupCompleted())
                .updatedAt(updated.getUpdatedAt())
                .message("Business information updated successfully.")
                .build();

        return ApiResponse.success("Business information updated successfully.", response);
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
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}