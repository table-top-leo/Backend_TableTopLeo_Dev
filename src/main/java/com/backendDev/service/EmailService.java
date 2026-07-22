package com.backendDev.service;

import com.backendDev.dto.InvoiceDetailsResponse;

public interface EmailService {

    void sendOtpEmail(String toEmail, String otp, String fullName);
    void sendPasswordResetEmail(String toEmail, String fullName, String resetUrl);

    // Invoice email — body is built entirely from data already collected
    // via InvoiceDetailsResponse (no separate invoice table).
    void sendInvoiceEmail(String toEmail, InvoiceDetailsResponse invoice);
}

