package com.backendDev.service;

import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.BusinessInformationResponse;
import com.backendDev.dto.BusinessSetupRequest;
import com.backendDev.dto.BusinessSetupResponse;

public interface BusinessService {

    ApiResponse<BusinessSetupResponse> setupBusiness(BusinessSetupRequest request);

    ApiResponse<BusinessInformationResponse> getBusinessInformation(String adminId);
}
