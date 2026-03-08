package com.example.spring.microservice.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateTokenResponse {

    private UUID userId;
    private String role;
    private boolean valid;

    public static ValidateTokenResponse valid(UUID userId, String role) {
        return ValidateTokenResponse.builder()
                .userId(userId)
                .role(role)
                .valid(true)
                .build();
    }

    public static ValidateTokenResponse invalid() {
        return ValidateTokenResponse.builder()
                .valid(false)
                .build();
    }
}