package com.backendDev.service;

import com.backendDev.dto.*;

public interface AuthService {

    ApiResponse<RegisterResponse> register(RegisterRequest request);

    ApiResponse<String> verifyOtp(VerifyOtpRequest request);

    ApiResponse<CreatePasswordResponse> createPassword(CreatePasswordRequest request);
}
