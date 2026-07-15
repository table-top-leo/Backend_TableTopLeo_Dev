package com.backendDev.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LanguageRequest {
    @NotBlank(message = "Language code is required")
    private String languageCode;
    @NotBlank(message = "Language name is required")
    private String languageName;
}
