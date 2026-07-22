package com.backendDev.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SendInvoiceEmailRequest {

    @NotBlank(message = "email is required")
    @Email(message = "A valid email address is required")
    private String email;
}
