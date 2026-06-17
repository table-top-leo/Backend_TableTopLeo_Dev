package com.backendDev.constants;

/**
 * Central registry of all API endpoint path constants.
 * Use these in controller @PostMapping / @GetMapping annotations
 * instead of hardcoded strings.
 */
public final class ApiConstants {

    private ApiConstants() {}

    // Auth endpoints (base: /api/auth)
    public static final String REGISTER_USER       = "/register";
    public static final String VERIFY_OTP          = "/verify-otp";
    public static final String CREATE_PASSWORD     = "/create-password";
    public static final String LOGIN_USER          = "/login";

    // Business setup endpoints (base: /api/business)
    public static final String BUSINESS_SETUP      = "/setup";

    // Business information endpoints (base: /api)
    public static final String BUSINESS_INFORMATION = "/business-information/{adminId}";
}
