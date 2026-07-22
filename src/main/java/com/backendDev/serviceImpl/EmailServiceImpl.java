package com.backendDev.serviceImpl;

import com.backendDev.dto.InvoiceDetailsResponse;
import com.backendDev.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String toEmail, String otp, String fullName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("TableTopLeo - Email Verification OTP");
            helper.setText(buildOtpEmailBody(fullName, otp), true);
            mailSender.send(mimeMessage);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (MessagingException ex) {
            log.error("Failed to send OTP email to {}: {}", toEmail, ex.getMessage());
            throw new RuntimeException("Failed to send verification email. Please try again.");
        }
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String fullName, String resetUrl) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("TableTopLeo - Reset Your Password");
            helper.setText(buildPasswordResetEmailBody(fullName, resetUrl), true);
            mailSender.send(mimeMessage);
            log.info("Password reset email sent successfully to: {}", toEmail);
        } catch (MessagingException ex) {
            log.error("Failed to send password reset email to {}: {}", toEmail, ex.getMessage());
            throw new RuntimeException("Failed to send password reset email. Please try again.");
        }
    }

    @Override
    public void sendInvoiceEmail(String toEmail, InvoiceDetailsResponse invoice) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("TableTopLeo - Invoice #" + invoice.getInvoiceNumber());
            helper.setText(buildInvoiceEmailBody(invoice), true);
            mailSender.send(mimeMessage);
            log.info("Invoice email sent successfully to: {} for order: {}", toEmail, invoice.getOrderId());
        } catch (MessagingException ex) {
            log.error("Failed to send invoice email to {}: {}", toEmail, ex.getMessage());
            throw new RuntimeException("Failed to send invoice email. Please try again.");
        }
    }

    private String buildOtpEmailBody(String fullName, String otp) {
        return "<!DOCTYPE html>"
                + "<html lang='en'>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Email Verification</title>"
                + "</head>"
                + "<body style='margin:0;padding:0;background-color:#f6f8fa;font-family:-apple-system,BlinkMacSystemFont,\"Segoe UI\",Helvetica,Arial,sans-serif;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f6f8fa;padding:40px 0;'>"
                + "<tr><td align='center'>"
                + "<table width='520' cellpadding='0' cellspacing='0' style='background-color:#ffffff;border:1px solid #d0d7de;border-radius:6px;overflow:hidden;'>"
                + "<tr><td style='padding:32px 40px 0 40px;text-align:center;'>"
                + "<p style='margin:0 0 24px 0;font-size:20px;font-weight:400;color:#1f2328;line-height:1.5;'>"
                + "Please verify your identity, <strong>" + fullName + "</strong>"
                + "</p>"
                + "</td></tr>"
                + "<tr><td style='padding:0 40px;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f6f8fa;border:1px solid #d0d7de;border-radius:6px;'>"
                + "<tr><td style='padding:24px 32px;'>"
                + "<p style='margin:0 0 16px 0;font-size:14px;color:#1f2328;line-height:1.5;'>Here is your TableTopLeo verification code:</p>"
                + "<p style='margin:0 0 16px 0;font-size:32px;font-weight:300;color:#1f2328;letter-spacing:8px;text-align:center;font-family:\"SFMono-Regular\",Consolas,monospace;'>" + otp + "</p>"
                + "<p style='margin:0 0 10px 0;font-size:14px;color:#1f2328;line-height:1.5;'>This code is valid for <strong>5 minutes</strong> and can only be used once.</p>"
                + "<p style='margin:0;font-size:14px;color:#1f2328;line-height:1.5;'>Thanks,<br>TableTopLeo Team</p>"
                + "</td></tr></table></td></tr>"
                + "<tr><td style='padding:24px 40px 32px 40px;'>"
                + "<p style='margin:0;font-size:12px;color:#57606a;line-height:1.5;'>You're receiving this email because a verification code was requested for your TableTopLeo account. If this wasn't you, please ignore this email.</p>"
                + "</td></tr></table></td></tr></table></body></html>";
    }

    private String buildPasswordResetEmailBody(String fullName, String resetUrl) {
        String firstName = fullName != null && fullName.contains(" ")
                ? fullName.split(" ")[0]
                : fullName;

        return "<!DOCTYPE html>"
                + "<html lang='en'>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1.0'>"
                + "<title>Reset Your Password</title>"
                + "</head>"
                + "<body style='margin:0;padding:0;background:#f4f6f9;font-family:-apple-system,BlinkMacSystemFont,\"Segoe UI\",Helvetica,Arial,sans-serif;'>"

                + "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f4f6f9;padding:40px 0;'>"
                + "<tr><td align='center'>"
                + "<table width='540' cellpadding='0' cellspacing='0' style='max-width:540px;background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 2px 12px rgba(0,0,0,0.08);'>"

                + "<tr><td style='background:linear-gradient(135deg,#1a1a2e 0%,#16213e 50%,#0f3460 100%);padding:32px 40px;text-align:center;'>"
                + "<div style='display:inline-flex;align-items:center;gap:10px;'>"
                + "<div style='width:36px;height:36px;background:#f59e0b;border-radius:8px;display:inline-block;line-height:36px;text-align:center;font-size:18px;'>🍽️</div>"
                + "<span style='font-size:20px;font-weight:800;color:#ffffff;letter-spacing:-0.3px;margin-left:8px;'>TableTop Leo</span>"
                + "</div>"
                + "</td></tr>"

                + "<tr><td style='padding:36px 40px 28px;text-align:center;'>"
                + "<div style='width:60px;height:60px;background:#fef3c7;border-radius:50%;margin:0 auto 20px;display:flex;align-items:center;justify-content:center;font-size:28px;line-height:60px;'>🔑</div>"
                + "<h1 style='margin:0 0 10px;font-size:22px;font-weight:800;color:#111827;letter-spacing:-0.4px;'>Reset Your Password</h1>"
                + "<p style='margin:0;font-size:14.5px;color:#6b7280;line-height:1.6;'>Hi <strong style='color:#111827;'>" + firstName + "</strong>, we received a request to reset your password.</p>"
                + "</td></tr>"

                + "<tr><td style='padding:0 40px;'>"
                + "<div style='background:#f9fafb;border:1px solid #f3f4f6;border-radius:10px;padding:24px;text-align:center;'>"
                + "<p style='margin:0 0 18px;font-size:14px;color:#374151;line-height:1.6;'>Click the button below to create a new password. This link will expire in <strong>30 minutes</strong>.</p>"
                + "<a href='" + resetUrl + "' style='display:inline-block;background:#f59e0b;color:#ffffff;text-decoration:none;font-size:14.5px;font-weight:700;padding:13px 36px;border-radius:9px;letter-spacing:0.2px;'>Reset Password →</a>"
                + "</div>"
                + "</td></tr>"

                + "<tr><td style='padding:24px 40px;'>"
                + "<div style='background:#fef2f2;border:1px solid #fecaca;border-radius:8px;padding:14px 18px;'>"
                + "<p style='margin:0;font-size:12.5px;color:#7f1d1d;line-height:1.6;'>🔒 <strong>Didn't request this?</strong> If you didn't request a password reset, you can safely ignore this email. Your password will remain unchanged.</p>"
                + "</div>"
                + "</td></tr>"

                + "<tr><td style='padding:0 40px 32px;'>"
                + "<p style='margin:0;font-size:12px;color:#9ca3af;line-height:1.6;'>If the button above doesn't work, copy and paste this link into your browser:<br>"
                + "<a href='" + resetUrl + "' style='color:#6b7280;word-break:break-all;font-size:11.5px;'>" + resetUrl + "</a></p>"
                + "</td></tr>"

                + "<tr><td style='background:#f9fafb;border-top:1px solid #f3f4f6;padding:20px 40px;text-align:center;'>"
                + "<p style='margin:0 0 4px;font-size:12.5px;font-weight:700;color:#374151;'>TableTop Leo Team</p>"
                + "</td></tr>"

                + "</table></td></tr></table>"
                + "</body></html>";
    }

    private String buildInvoiceEmailBody(InvoiceDetailsResponse invoice) {
        String formattedDate = invoice.getCreatedAt() != null
                ? invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("d/M/yyyy hh:mm a"))
                : "";

        StringBuilder itemRows = new StringBuilder();
        if (invoice.getItems() != null) {
            for (InvoiceDetailsResponse.InvoiceItemDto item : invoice.getItems()) {
                itemRows.append("<tr>")
                        .append("<td style='padding:10px 8px;border-bottom:1px solid #f0f0f0;font-size:13.5px;color:#1f2328;'>")
                        .append(item.getProductName()).append("</td>")
                        .append("<td style='padding:10px 8px;border-bottom:1px solid #f0f0f0;font-size:13.5px;color:#1f2328;text-align:center;'>")
                        .append(item.getQuantity()).append("</td>")
                        .append("<td style='padding:10px 8px;border-bottom:1px solid #f0f0f0;font-size:13.5px;color:#1f2328;text-align:right;'>")
                        .append(formatAmount(item.getUnitPrice())).append("</td>")
                        .append("<td style='padding:10px 8px;border-bottom:1px solid #f0f0f0;font-size:13.5px;color:#1f2328;text-align:right;font-weight:600;'>")
                        .append(formatAmount(item.getLineTotal())).append("</td>")
                        .append("</tr>");
            }
        }

        return "<!DOCTYPE html>"
                + "<html lang='en'>"
                + "<head><meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1.0'><title>Invoice</title></head>"
                + "<body style='margin:0;padding:0;background:#f4f6f9;font-family:-apple-system,BlinkMacSystemFont,\"Segoe UI\",Helvetica,Arial,sans-serif;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f4f6f9;padding:40px 0;'>"
                + "<tr><td align='center'>"
                + "<table width='560' cellpadding='0' cellspacing='0' style='max-width:560px;background:#ffffff;border-radius:10px;overflow:hidden;box-shadow:0 2px 12px rgba(0,0,0,0.08);'>"

                + "<tr><td style='padding:28px 36px;border-bottom:2px solid #111827;'>"
                + "<table width='100%'><tr>"
                + "<td style='font-size:20px;font-weight:800;color:#111827;'>" + safe(invoice.getBusinessName()) + "</td>"
                + "<td style='text-align:right;'>"
                + "<div style='font-size:11px;letter-spacing:1px;color:#9ca3af;'>TAX INVOICE</div>"
                + "<div style='font-size:18px;font-weight:800;color:#111827;'>" + safe(invoice.getInvoiceNumber()) + "</div>"
                + "<div style='font-size:12px;color:#6b7280;'>" + formattedDate + "</div>"
                + "</td></tr></table>"
                + "</td></tr>"

                + "<tr><td style='padding:24px 36px;border-bottom:1px solid #f0f0f0;'>"
                + "<table width='100%'><tr>"
                + "<td style='vertical-align:top;'>"
                + "<div style='font-size:11px;letter-spacing:1px;color:#9ca3af;margin-bottom:4px;'>BILL TO</div>"
                + "<div style='font-size:14px;font-weight:700;color:#111827;'>" + safe(invoice.getCustomerName()) + "</div>"
                + "<div style='font-size:13px;color:#6b7280;'>" + safe(invoice.getCustomerPhone()) + "</div>"
                + "<div style='font-size:13px;color:#6b7280;'>" + safe(invoice.getOrderType())
                + (invoice.getTableNumber() != null && !invoice.getTableNumber().isBlank() ? " • Table " + invoice.getTableNumber() : "") + "</div>"
                + "</td>"
                + "<td style='vertical-align:top;text-align:right;'>"
                + "<div style='font-size:11px;letter-spacing:1px;color:#9ca3af;margin-bottom:4px;'>ORDER REFERENCE</div>"
                + "<div style='font-size:13px;color:#111827;'>" + safe(invoice.getOrderId()) + "</div>"
                + "<div style='font-size:13px;color:#6b7280;'>" + safe(invoice.getPaymentMethod()) + "</div>"
                + "</td></tr></table>"
                + "</td></tr>"

                + "<tr><td style='padding:20px 36px 0 36px;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0'>"
                + "<tr>"
                + "<th style='text-align:left;padding:8px;font-size:11px;letter-spacing:0.5px;color:#9ca3af;border-bottom:2px solid #111827;'>DESCRIPTION</th>"
                + "<th style='text-align:center;padding:8px;font-size:11px;letter-spacing:0.5px;color:#9ca3af;border-bottom:2px solid #111827;'>QTY</th>"
                + "<th style='text-align:right;padding:8px;font-size:11px;letter-spacing:0.5px;color:#9ca3af;border-bottom:2px solid #111827;'>RATE</th>"
                + "<th style='text-align:right;padding:8px;font-size:11px;letter-spacing:0.5px;color:#9ca3af;border-bottom:2px solid #111827;'>AMOUNT</th>"
                + "</tr>"
                + itemRows
                + "</table>"
                + "</td></tr>"

                + "<tr><td style='padding:16px 36px 28px 36px;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0'>"
                + "<tr><td style='padding:6px 0;font-size:13.5px;color:#6b7280;'>Subtotal</td>"
                + "<td style='padding:6px 0;font-size:13.5px;color:#1f2328;text-align:right;'>" + formatAmount(invoice.getSubtotal()) + "</td></tr>"
                + "<tr><td style='padding:6px 0 14px 0;font-size:13.5px;color:#6b7280;border-bottom:1px solid #111827;'>GST</td>"
                + "<td style='padding:6px 0 14px 0;font-size:13.5px;color:#1f2328;text-align:right;border-bottom:1px solid #111827;'>" + formatAmount(invoice.getGstAmount()) + "</td></tr>"
                + "<tr><td style='padding:14px 0 0 0;font-size:16px;font-weight:800;color:#111827;'>TOTAL</td>"
                + "<td style='padding:14px 0 0 0;font-size:16px;font-weight:800;color:#111827;text-align:right;'>" + formatAmount(invoice.getGrandTotal()) + "</td></tr>"
                + "</table>"
                + "</td></tr>"

                + "<tr><td style='padding:0 36px 28px 36px;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f9fafb;border:1px solid #f0f0f0;border-radius:8px;'>"
                + "<tr><td style='padding:14px 20px;'>"
                + "<span style='font-size:11px;letter-spacing:0.5px;color:#9ca3af;'>PAYMENT STATUS</span><br>"
                + "<span style='font-size:14px;font-weight:700;color:#15803d;'>" + safe(invoice.getPaymentStatus()) + "</span>"
                + "</td><td style='padding:14px 20px;text-align:right;'>"
                + "<span style='font-size:11px;letter-spacing:0.5px;color:#9ca3af;'>METHOD</span><br>"
                + "<span style='font-size:14px;font-weight:700;color:#111827;'>" + safe(invoice.getPaymentMethod()) + "</span>"
                + "</td></tr></table>"
                + "</td></tr>"

                + "<tr><td style='background:#111827;padding:16px 36px;text-align:center;'>"
                + "<span style='font-size:12.5px;color:#e5e7eb;'>Thank you for your visit! · Powered by TableTop Leo</span>"
                + "</td></tr>"

                + "</table></td></tr></table>"
                + "</body></html>";
    }

    private String formatAmount(BigDecimal amount) {
        return "₹" + (amount != null ? amount.setScale(2, java.math.RoundingMode.HALF_UP) : "0.00");
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
