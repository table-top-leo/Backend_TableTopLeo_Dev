package com.backendDev.serviceImpl;

import com.backendDev.common.AppException;
import com.backendDev.common.OtpGenerator;
import com.backendDev.dto.*;
import com.backendDev.model.OtpVerification;
import com.backendDev.model.User;
import com.backendDev.repo.OtpVerificationRepository;
import com.backendDev.repo.UserRepository;
import com.backendDev.service.AuthService;
import com.backendDev.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final EmailService emailService;
    private final OtpGenerator otpGenerator;
    private final PasswordEncoder passwordEncoder;

    private static final int OTP_VALIDITY_MINUTES = 5;

    @Override
    @Transactional
    public ApiResponse<RegisterResponse> register(RegisterRequest request) {
        // Check if email already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(
                "Email is already registered. Please use a different email or proceed to login.",
                HttpStatus.CONFLICT
            );
        }

        // Save user record with basic details
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .mobileNumber(request.getMobileNumber())
                .emailVerified(false)
                .accountStatus("ACTIVE")
                .build();
        userRepository.save(user);
        log.info("New user pre-registered with email: {}", request.getEmail());

        // Generate OTP and save
        String otp = otpGenerator.generate();
        otpVerificationRepository.deleteByEmail(request.getEmail());

        OtpVerification otpVerification = OtpVerification.builder()
                .email(request.getEmail())
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES))
                .verified(false)
                .build();
        otpVerificationRepository.save(otpVerification);

        // Send OTP email
        emailService.sendOtpEmail(request.getEmail(), otp, request.getFullName());

        RegisterResponse data = RegisterResponse.builder()
                .email(request.getEmail())
                .message("OTP sent to your email. Valid for 5 minutes.")
                .build();

        return ApiResponse.success("Registration initiated. Please verify your email.", data);
    }

    @Override
    @Transactional
    public ApiResponse<String> verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("No registration found for this email.", HttpStatus.NOT_FOUND));

        OtpVerification otpVerification = otpVerificationRepository
                .findTopByEmailOrderByCreatedAtDesc(request.getEmail())
                .orElseThrow(() -> new AppException("No OTP found for this email. Please request a new OTP.", HttpStatus.NOT_FOUND));

        if (otpVerification.getVerified()) {
            throw new AppException("Email is already verified. Please proceed to set your password.", HttpStatus.BAD_REQUEST);
        }

        if (LocalDateTime.now().isAfter(otpVerification.getExpiryTime())) {
            throw new AppException("OTP has expired. Please request a new OTP.", HttpStatus.BAD_REQUEST);
        }

        if (!otpVerification.getOtp().equals(request.getOtp())) {
            throw new AppException("Invalid OTP. Please check and try again.", HttpStatus.BAD_REQUEST);
        }

        // Mark OTP as verified
        otpVerification.setVerified(true);
        otpVerificationRepository.save(otpVerification);

        // Mark user email as verified
        user.setEmailVerified(true);
        userRepository.save(user);

        log.info("Email verified successfully for: {}", request.getEmail());
        return ApiResponse.success("Email verified successfully. Please proceed to set your password.", request.getEmail());
    }

    @Override
    @Transactional
    public ApiResponse<CreatePasswordResponse> createPassword(CreatePasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("No registration found for this email.", HttpStatus.NOT_FOUND));

        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new AppException("Email is not verified. Please verify your email first.", HttpStatus.FORBIDDEN);
        }

        if (user.getPasswordHash() != null) {
            throw new AppException("Password already set for this account. Please proceed to login.", HttpStatus.BAD_REQUEST);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException("Password and confirm password do not match.", HttpStatus.BAD_REQUEST);
        }

        // Encrypt and save password
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Generate admin_id using the database-assigned id
        String adminId = String.format("ADM%06d", user.getId());
        user.setAdminId(adminId);

        userRepository.save(user);
        log.info("Registration completed for admin: {}", adminId);

        CreatePasswordResponse data = CreatePasswordResponse.builder()
                .adminId(adminId)
                .email(user.getEmail())
                .message("Registration completed successfully.")
                .build();

        return ApiResponse.success("Account created successfully. Please proceed to business setup.", data);
    }
}
