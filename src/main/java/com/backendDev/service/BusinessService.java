package com.backendDev.service;

import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.BusinessInformationResponse;
import com.backendDev.dto.BusinessSetupRequest;
import com.backendDev.dto.BusinessSetupResponse;
import com.backendDev.dto.BusinessUpdateRequest;
import com.backendDev.dto.BusinessUpdateResponse;

public interface BusinessService {

    ApiResponse<BusinessSetupResponse> setupBusiness(BusinessSetupRequest request);

    ApiResponse<BusinessInformationResponse> getBusinessInformation(String adminId);

    ApiResponse<BusinessUpdateResponse> updateBusinessInformation(String adminId, BusinessUpdateRequest request);
}