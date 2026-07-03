package com.backendDev.service;

import com.backendDev.dto.ApiResponse;
import com.backendDev.dto.ForgotPasswordRequest;
import com.backendDev.dto.ResetPasswordRequest;
import com.backendDev.dto.ValidateResetTokenRequest;

public interface ForgotPasswordService {

    ApiResponse<Void> forgotPassword(ForgotPasswordRequest request);

    ApiResponse<Void> validateResetToken(ValidateResetTokenRequest request);

    ApiResponse<Void> resetPassword(ResetPasswordRequest request);
}
