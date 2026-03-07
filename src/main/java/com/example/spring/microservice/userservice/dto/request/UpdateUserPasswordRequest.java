package com.example.spring.microservice.userservice.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserPasswordRequest {

    @NotBlank(message = "Password hash must not be blank")
    private String passwordHash;
}
