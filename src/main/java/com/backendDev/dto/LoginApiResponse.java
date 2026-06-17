package com.backendDev.dto;

import lombok.*;

/**
 * Dedicated login response — keeps token at the top level
 * alongside success/message rather than nesting it under a data field.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginApiResponse {

    private boolean success;
    private String message;
    private String token;
}
