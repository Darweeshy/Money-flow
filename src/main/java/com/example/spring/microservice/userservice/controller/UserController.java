package com.example.spring.microservice.userservice.controller;

import com.example.spring.microservice.userservice.dto.request.UpdateUserBalanceRequest;
import com.example.spring.microservice.userservice.dto.request.UpdateUserRequest;
import com.example.spring.microservice.userservice.dto.request.UpdateUserStatusRequest;
import com.example.spring.microservice.userservice.dto.response.UserResponse;
import com.example.spring.microservice.userservice.dto.response.UserSummaryResponse;
import com.example.spring.microservice.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ── GET /users/me ────────────────────────────────────────
    @GetMapping("/users/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponse> getAuthenticatedUser(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(
                userService.getAuthenticatedUser(UUID.fromString(userId)));
    }

    // ── PUT /users/me ────────────────────────────────────────
    @PutMapping("/users/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponse> updateAuthenticatedUser(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(
                userService.updateAuthenticatedUser(UUID.fromString(userId), request));
    }

    // ── GET /users/{userId} ──────────────────────────────────
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // ── GET /users ───────────────────────────────────────────
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserSummaryResponse>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    // ── Internal endpoints (protected by InternalAuthFilter) ─

    @GetMapping("/internal/users/{userId}")
    public ResponseEntity<UserResponse> getUserByIdInternal(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/internal/users/by-email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(
            @PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/internal/users/by-identifier/{identifier}")
    public ResponseEntity<UserResponse> getUserByIdentifier(
            @PathVariable String identifier) {
        return ResponseEntity.ok(userService.getUserByIdentifier(identifier));
    }

    @GetMapping("/internal/users/by-account/{accountNumber}")
    public ResponseEntity<UserResponse> getUserByAccountNumber(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(userService.getUserByAccountNumber(accountNumber));
    }

    @PutMapping("/internal/users/{userId}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(userService.updateUserStatus(userId, request));
    }

    @PutMapping("/internal/users/{userId}/balance")
    public ResponseEntity<UserResponse> updateUserBalance(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserBalanceRequest request) {
        return ResponseEntity.ok(userService.updateUserBalance(userId, request));
    }

    @GetMapping("/internal/users/{userId}/exists")
    public ResponseEntity<Boolean> userExists(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(userService.userExists(userId));
    }
}