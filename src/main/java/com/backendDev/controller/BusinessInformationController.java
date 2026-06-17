package com.backendDev.controller;

import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.BusinessInformationResponse;
import com.backendDev.dto.BusinessUpdateRequest;
import com.backendDev.dto.BusinessUpdateResponse;
import com.backendDev.service.BusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BusinessInformationController {

    private final BusinessService businessService;

    @GetMapping("/business-information/{adminId}")
    public ResponseEntity<ApiResponse<BusinessInformationResponse>> getBusinessInformation(
            @PathVariable String adminId) {
        ApiResponse<BusinessInformationResponse> response =
                businessService.getBusinessInformation(adminId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/business-information/{adminId}")
    public ResponseEntity<ApiResponse<BusinessUpdateResponse>> updateBusinessInformation(
            @PathVariable String adminId,
            @Valid @RequestBody BusinessUpdateRequest request) {
        ApiResponse<BusinessUpdateResponse> response =
                businessService.updateBusinessInformation(adminId, request);
        return ResponseEntity.ok(response);
    }
}