package com.example.spring.microservice.userservice.dto.request;

import com.example.spring.microservice.userservice.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusRequest {

    @NotNull(message = "Account status must not be null")
    private AccountStatus accountStatus;
}
