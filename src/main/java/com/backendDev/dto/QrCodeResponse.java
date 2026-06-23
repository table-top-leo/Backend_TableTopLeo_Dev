package com.backendDev.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for QR code generation.
 * Returns the public menu URL and the base64 QR image for the frontend to render/download.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrCodeResponse {

    private Long qrId;
    private String adminId;
    private String businessId;
    private String qrUrl;              // the public menu URL: http://localhost:3000/menu/{businessId}
    private String qrImageBase64;      // "data:image/png;base64,..." — ready to drop in <img src="">
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
