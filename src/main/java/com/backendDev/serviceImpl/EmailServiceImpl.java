package com.backendDev.serviceImpl;

import com.backendDev.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("TableTopLeo - Email Verification OTP");
            message.setText(buildOtpEmailBody(fullName, otp));
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send OTP email to {}: {}", toEmail, ex.getMessage());
            throw new RuntimeException("Failed to send verification email. Please try again.");
        }
    }

    private String buildOtpEmailBody(String fullName, String otp) {
        return "Hello " + fullName + ",\n\n"
                + "Thank you for registering with TableTopLeo!\n\n"
                + "Your email verification OTP is:\n\n"
                + "  " + otp + "\n\n"
                + "This OTP is valid for 5 minutes.\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Regards,\n"
                + "TableTopLeo Team";
    }
}
