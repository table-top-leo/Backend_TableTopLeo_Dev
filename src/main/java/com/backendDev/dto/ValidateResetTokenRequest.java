package com.backendDev.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidateResetTokenRequest {

    @NotBlank(message = "Token is required.")
    private String token;
}