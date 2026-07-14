package com.backendDev.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginApiResponse {

    private boolean success;
    private String message;
    private String token;
    private String adminId;
    private String fullName;
    private String email;
    private String businessId;
    private String logoUrl;

    private String currencyCode;
}
