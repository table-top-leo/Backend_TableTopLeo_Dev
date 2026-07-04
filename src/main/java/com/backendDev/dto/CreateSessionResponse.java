package com.backendDev.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateSessionResponse {
    private String sessionId;
    private String businessId;
    private String adminId;
    private String sessionStatus;
}