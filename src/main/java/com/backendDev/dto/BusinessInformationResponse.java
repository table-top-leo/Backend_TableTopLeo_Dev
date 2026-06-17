package com.backendDev.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessInformationResponse {

    private String businessId;
    private String adminId;
    private String businessType;
    private String businessName;
    private String businessEmail;
    private String businessPhone;
    private String logoUrl;
    private String gstNumber;
    private String licenseNumber;
    private String website;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String workingDays;
    private String timezone;
    private String businessDescription;
    private Boolean setupCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
