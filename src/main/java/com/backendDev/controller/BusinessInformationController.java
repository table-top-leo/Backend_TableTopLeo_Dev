package com.backendDev.controller;

import com.backendDev.constants.ApiConstants;
import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.BusinessInformationResponse;
import com.backendDev.service.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BusinessInformationController {

    private final BusinessService businessService;

    /**
     * Fetch business information for a given adminId.
     * Requires a valid JWT in the Authorization header.
     *
     * GET /api/business-information/{adminId}
     */
    @GetMapping(ApiConstants.BUSINESS_INFORMATION)
    public ResponseEntity<ApiResponse<BusinessInformationResponse>> getBusinessInformation(
            @PathVariable String adminId) {
        ApiResponse<BusinessInformationResponse> response = businessService.getBusinessInformation(adminId);
        return ResponseEntity.ok(response);
    }
}
