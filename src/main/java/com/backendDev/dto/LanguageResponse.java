package com.backendDev.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LanguageResponse {
    private String adminId;
    private String languageCode;
    private String languageName;
    private String message;
}
