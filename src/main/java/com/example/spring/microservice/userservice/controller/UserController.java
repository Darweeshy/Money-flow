package com.example.spring.microservice.userservice.controller;

import com.example.spring.microservice.userservice.dto.request.UpdateUserBalanceRequest;
import com.example.spring.microservice.userservice.dto.request.UpdateUserPasswordRequest;
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
    // Returns the authenticated user's own profile and balance

    @GetMapping("/users/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponse> getAuthenticatedUser(
            @RequestHeader("X-User-Id") String userId) {

        return ResponseEntity.ok(
                userService.getAuthenticatedUser(UUID.fromString(userId))
        );
    }

    // ── PUT /users/me ────────────────────────────────────────
    // Update the authenticated user's own display name or email

    @PutMapping("/users/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponse> updateAuthenticatedUser(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(
                userService.updateAuthenticatedUser(UUID.fromString(userId), request)
        );
    }

    // ── GET /users/{userId} ──────────────────────────────────
    // Admin only — retrieve any user's full profile

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                userService.getUserById(userId)
        );
    }

    // ── GET /users ───────────────────────────────────────────
    // Admin only — paginated list of all users

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserSummaryResponse>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(
                userService.getAllUsers(pageable)
        );
    }

    // ── PATCH /internal/users/{userId}/status ────────────────
    // Internal — called by Manage User Service to lock/unlock/suspend

    @PatchMapping("/internal/users/{userId}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        return ResponseEntity.ok(
                userService.updateUserStatus(userId, request)
        );
    }

    // ── PATCH /internal/users/{userId}/balance ───────────────
    // Internal — called by Transfer Service consumer to debit/credit

    @PatchMapping("/internal/users/{userId}/balance")
    public ResponseEntity<UserResponse> updateUserBalance(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserBalanceRequest request) {

        return ResponseEntity.ok(
                userService.updateUserBalance(userId, request)
        );
    }

    // ── PATCH /internal/users/{userId}/password ──────────────
    // Internal — called by Auth Service after password reset

    @PatchMapping("/internal/users/{userId}/password")
    public ResponseEntity<Void> updateUserPassword(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserPasswordRequest request) {

        userService.updateUserPassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    // ── GET /internal/users/{userId}/exists ──────────────────
    // Internal — called by Transfer Service to verify recipient exists

    @GetMapping("/internal/users/{userId}/exists")
    public ResponseEntity<Boolean> userExists(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                userService.userExists(userId)
        );
    }
}