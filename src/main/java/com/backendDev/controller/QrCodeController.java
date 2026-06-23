package com.backendDev.controller;

import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.CustomerMenuResponse;
import com.backendDev.dto.QrCodeResponse;
import com.backendDev.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Phase 5 — QR Code Controller
 *
 * /api/qr/**  — JWT-protected (admin generates/views their QR)
 * /api/menu/** — PUBLIC (no auth, any customer who scans QR can call this)
 *
 * The SecurityConfig must permit /api/menu/** without authentication.
 */
@RestController
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;

    /**
     * POST /api/qr/generate
     * Admin triggers QR generation after payment setup.
     * JWT required. businessId is resolved automatically.
     */
    @PostMapping("/api/qr/generate")
    public ResponseEntity<ApiResponse<QrCodeResponse>> generateQrCode() {
        return ResponseEntity.ok(qrCodeService.generateQrCode());
    }

    /**
     * GET /api/qr/my
     * Admin retrieves their existing QR code for dashboard display.
     */
    @GetMapping("/api/qr/my")
    public ResponseEntity<ApiResponse<QrCodeResponse>> getMyQrCode() {
        return ResponseEntity.ok(qrCodeService.getQrCode());
    }

    /**
     * GET /api/menu/{businessId}
     * PUBLIC endpoint — No JWT required.
     * This is what the customer's browser calls after scanning the QR.
     * Returns full business info + categories + products.
     */
    @GetMapping("/api/menu/{businessId}")
    public ResponseEntity<ApiResponse<CustomerMenuResponse>> getPublicMenu(
            @PathVariable String businessId) {
        return ResponseEntity.ok(qrCodeService.getPublicMenu(businessId));
    }
}
