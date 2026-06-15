package com.backendDev.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessSetupRequest {

    @NotBlank(message = "Admin ID is required")
    private String adminId;

    @NotBlank(message = "Business type is required")
    private String businessType;

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotBlank(message = "Business email is required")
    @Email(message = "Invalid business email format")
    private String businessEmail;

    @NotBlank(message = "Business phone is required")
    private String businessPhone;

    private String logoUrl;

    private String gstNumber;

    private String licenseNumber;

    private String website;

    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotBlank(message = "Opening time is required")
    private String openingTime;

    @NotBlank(message = "Closing time is required")
    private String closingTime;

    @NotBlank(message = "Working days is required")
    private String workingDays;

    @NotBlank(message = "Timezone is required")
    private String timezone;

    private String businessDescription;
}
