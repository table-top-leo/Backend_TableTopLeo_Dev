package com.backendDev.controller;

import com.backendDev.constants.ApiConstants;
import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.BusinessSetupRequest;
import com.backendDev.dto.BusinessSetupResponse;
import com.backendDev.service.BusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;


    /**
     * Business Setup — immediately after registration.
     * Frontend submits business info using adminId received from create-password.
     *
     * POST /api/business/setup
     */
    @PostMapping(ApiConstants.BUSINESS_SETUP)
    public ResponseEntity<ApiResponse<BusinessSetupResponse>> setupBusiness(
            @Valid @RequestBody BusinessSetupRequest request) {
        ApiResponse<BusinessSetupResponse> response = businessService.setupBusiness(request);
        return ResponseEntity.ok(response);
    }
}
