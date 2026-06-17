package com.backendDev.common;

import org.springframework.http.HttpStatus;

/**
 * Thrown when email is not found or password does not match during login.
 * Uses a generic message intentionally to avoid disclosing whether
 * the email exists in the system.
 */
public class InvalidCredentialsException extends AppException {

    public InvalidCredentialsException() {
        super("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
}
