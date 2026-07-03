package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.dto.*;
import com.backendDev.model.PasswordResetToken;
import com.backendDev.model.User;
import com.backendDev.repo.PasswordResetTokenRepository;
import com.backendDev.repo.UserRepository;
import com.backendDev.service.EmailService;
import com.backendDev.service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final int TOKEN_EXPIRY_MINUTES = 30;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public ApiResponse<Void> forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !"ACTIVE".equals(user.getAccountStatus()) || !Boolean.TRUE.equals(user.getEmailVerified())) {
            return ApiResponse.success("If this email is registered, a reset link has been sent.");
        }

        passwordResetTokenRepository.invalidatePreviousTokens(user.getAdminId());

        String rawToken = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");

        PasswordResetToken token = PasswordResetToken.builder()
                .adminId(user.getAdminId())
                .email(email)
                .resetToken(rawToken)
                .expiryTime(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES))
                .used(false)
                .build();

        passwordResetTokenRepository.save(token);

        String resetUrl = frontendUrl + "/reset-password?token=" + rawToken;

        try {
            emailService.sendPasswordResetEmail(email, user.getFullName(), resetUrl);
            log.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", email, e.getMessage());
            throw new AppException("Failed to send reset email. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ApiResponse.success("If this email is registered, a reset link has been sent.");
    }

    @Override
    public ApiResponse<Void> validateResetToken(ValidateResetTokenRequest request) {
        PasswordResetToken token = passwordResetTokenRepository
                .findByResetToken(request.getToken())
                .orElseThrow(() -> new AppException("Invalid or expired reset link. Please request a new one.", HttpStatus.BAD_REQUEST));

        if (Boolean.TRUE.equals(token.getUsed())) {
            throw new AppException("This reset link has already been used. Please request a new one.", HttpStatus.BAD_REQUEST);
        }

        if (token.isExpired()) {
            throw new AppException("This reset link has expired. Please request a new one.", HttpStatus.BAD_REQUEST);
        }

        return ApiResponse.success("Token is valid.");
    }

    @Override
    @Transactional
    public ApiResponse<Void> resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException("Passwords do not match.", HttpStatus.BAD_REQUEST);
        }

        PasswordResetToken token = passwordResetTokenRepository
                .findByResetToken(request.getToken())
                .orElseThrow(() -> new AppException("Invalid or expired reset link. Please request a new one.", HttpStatus.BAD_REQUEST));

        if (Boolean.TRUE.equals(token.getUsed())) {
            throw new AppException("This reset link has already been used.", HttpStatus.BAD_REQUEST);
        }

        if (token.isExpired()) {
            throw new AppException("This reset link has expired. Please request a new one.", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByAdminId(token.getAdminId())
                .orElseThrow(() -> new AppException("User not found.", HttpStatus.NOT_FOUND));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        passwordResetTokenRepository.save(token);

        log.info("Password reset successfully for adminId: {}", token.getAdminId());

        return ApiResponse.success("Password has been reset successfully. You can now login with your new password.");
    }
}
