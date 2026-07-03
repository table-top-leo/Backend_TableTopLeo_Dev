package com.backendDev.service;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp, String fullName);
    void sendPasswordResetEmail(String toEmail, String fullName, String resetUrl);
}

