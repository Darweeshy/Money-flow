package com.example.spring.microservice.userservice.dto.response;
import com.example.spring.microservice.userservice.enums.AccountStatus;
import com.example.spring.microservice.userservice.enums.Role;
import com.example.spring.microservice.userservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID userId;
    private String email;
    private String displayName;
    private Role role;
    private AccountStatus accountStatus;
    private BigDecimal balance;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String accountNumber;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .accountNumber(user.getAccountNumber())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .balance(user.getBalance())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}