package com.backendDev.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SendInvoiceEmailResponse {
    private String orderId;
    private String email;
    private boolean emailSent;
    private String message;
}
