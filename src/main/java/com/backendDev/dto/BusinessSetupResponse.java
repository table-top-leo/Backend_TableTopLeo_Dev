package com.backendDev.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusinessSetupResponse {

    private String businessId;
    private String adminId;
    private String businessName;
    private String businessType;
    private String city;
    private String state;
    private String country;
    private String currencyCode;
    private String message;
}
