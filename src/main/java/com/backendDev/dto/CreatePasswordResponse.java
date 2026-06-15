package com.backendDev.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePasswordResponse {

    private String adminId;
    private String email;
    private String message;
}
