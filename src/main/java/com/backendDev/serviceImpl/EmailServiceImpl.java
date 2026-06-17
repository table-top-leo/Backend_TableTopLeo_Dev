package com.backendDev.serviceImpl;

import com.backendDev.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
                + "<p style='margin:0 0 16px 0;font-size:14px;color:#1f2328;line-height:1.5;'>"
                + "Here is your TableTopLeo verification code:"
                + "</p>"
                + "<p style='margin:0 0 16px 0;font-size:32px;font-weight:300;color:#1f2328;letter-spacing:8px;text-align:center;font-family:\"SFMono-Regular\",Consolas,\"Liberation Mono\",Menlo,monospace;'>"
                + otp
                + "</p>"
                + "<p style='margin:0 0 10px 0;font-size:14px;color:#1f2328;line-height:1.5;'>"
                + "This code is valid for <strong>5 minutes</strong> and can only be used once."
                + "</p>"
                + "<p style='margin:0 0 10px 0;font-size:14px;color:#1f2328;line-height:1.5;'>"
                + "<strong>Please don't share this code with anyone:</strong> we'll never ask for it on the phone or via email."
                + "</p>"
                + "<p style='margin:0;font-size:14px;color:#1f2328;line-height:1.5;'>"
                + "Thanks,<br>TableTopLeo Team"
                + "</p>"
                + "</td></tr>"
                + "</table>"
                + "</td></tr>"

                + "<tr><td style='padding:24px 40px 32px 40px;'>"
                + "<p style='margin:0;font-size:12px;color:#57606a;line-height:1.5;'>"
                + "You're receiving this email because a verification code was requested for your TableTopLeo account. "
                + "If this wasn't you, please ignore this email."
                + "</p>"
                + "</td></tr>"

                + "</table>"
                + "</td></tr>"
                + "</table>"
                + "</body>"
                + "</html>";
    }
}