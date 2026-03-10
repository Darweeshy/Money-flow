package com.example.spring.microservice.userservice.service;

import com.example.spring.microservice.userservice.dto.request.UpdateUserBalanceRequest;
import com.example.spring.microservice.userservice.dto.request.UpdateUserRequest;
import com.example.spring.microservice.userservice.dto.request.UpdateUserStatusRequest;
import com.example.spring.microservice.userservice.dto.response.UserResponse;
import com.example.spring.microservice.userservice.dto.response.UserSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    // ── User-facing ──────────────────────────────────────────

    // GET /users/me
    UserResponse getAuthenticatedUser(UUID userId);

    // PUT /users/me
    UserResponse updateAuthenticatedUser(UUID userId, UpdateUserRequest request);

    // ── Admin-facing ─────────────────────────────────────────

    // GET /users/{userId}
    UserResponse getUserById(UUID userId);

    // GET /users
    Page<UserSummaryResponse> getAllUsers(Pageable pageable);

    // ── Internal — called by other services ──────────────────

    // PATCH /users/{userId}/status — called by admin or management services
    UserResponse updateUserStatus(UUID userId, UpdateUserStatusRequest request);

    // PATCH /users/{userId}/balance — called by Transfer Service consumer
    UserResponse updateUserBalance(UUID userId, UpdateUserBalanceRequest request);

    // GET existence check — called by Transfer Service to verify recipient
    boolean userExists(UUID userId);

    // GET /internal/users/by-email/{email} — called by AuthService on login
    UserResponse getUserByEmail(String email);

    // GET /internal/users/by-identifier/{identifier} — called by AuthService for case-insensitive login (email or username)
    UserResponse getUserByIdentifier(String identifier);

    // GET /internal/users/by-account/{accountNumber} — called by Transfer Service
    UserResponse getUserByAccountNumber(String accountNumber);
}