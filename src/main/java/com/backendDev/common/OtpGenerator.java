package com.backendDev.common;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    public String generate() {
        int bound = (int) Math.pow(10, OTP_LENGTH);
        int otp = RANDOM.nextInt(bound);
        return String.format("%0" + OTP_LENGTH + "d", otp);
    }
}
