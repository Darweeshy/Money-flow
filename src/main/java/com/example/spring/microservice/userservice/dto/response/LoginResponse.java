package com.example.spring.microservice.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private boolean passwordChangeRequired;

    public static LoginResponse of(String accessToken,
                                   String refreshToken,
                                   long expiresIn,
                                   boolean passwordChangeRequired) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .passwordChangeRequired(passwordChangeRequired)
                .build();
    }
}