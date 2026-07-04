package com.backendDev.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateSessionRequest {
    @NotBlank(message = "businessId is required")
    private String businessId;
    private String tableNumber;
}