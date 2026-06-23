package com.backendDev.service;

import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.CustomerMenuResponse;
import com.backendDev.dto.QrCodeResponse;

public interface QrCodeService {

    ApiResponse<QrCodeResponse> generateQrCode();

    ApiResponse<QrCodeResponse> getQrCode();

    ApiResponse<CustomerMenuResponse> getPublicMenu(String businessId);
}
