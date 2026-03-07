package com.example.spring.microservice.userservice.dto.response;
import com.example.spring.microservice.userservice.enums.AccountStatus;
import com.example.spring.microservice.userservice.enums.Role;
import com.example.spring.microservice.userservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {

    private UUID userId;
    private String email;
    private String displayName;
    private Role role;
    private AccountStatus accountStatus;

    public static UserSummaryResponse from(User user) {
        return UserSummaryResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .accountStatus(user.getAccountStatus())
                .build();
    }
}