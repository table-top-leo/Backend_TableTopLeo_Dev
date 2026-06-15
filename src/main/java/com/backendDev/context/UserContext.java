package com.backendDev.context;

/**
 * Holds the authenticated user's details for the current request thread.
 * Will be populated by JwtFilter in Phase 2 when login is implemented.
 */
public class UserContext {

    private static final ThreadLocal<String> CURRENT_ADMIN_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_EMAIL = new ThreadLocal<>();

    public static void setAdminId(String adminId) {
        CURRENT_ADMIN_ID.set(adminId);
    }

    public static String getAdminId() {
        return CURRENT_ADMIN_ID.get();
    }

    public static void setEmail(String email) {
        CURRENT_EMAIL.set(email);
    }

    public static String getEmail() {
        return CURRENT_EMAIL.get();
    }

    public static void clear() {
        CURRENT_ADMIN_ID.remove();
        CURRENT_EMAIL.remove();
    }
}
