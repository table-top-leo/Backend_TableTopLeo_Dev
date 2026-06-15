package com.backendDev.service;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp, String fullName);
}
