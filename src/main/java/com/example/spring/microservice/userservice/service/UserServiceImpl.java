package com.example.spring.microservice.userservice.service;

import com.example.spring.microservice.userservice.dto.request.UpdateUserBalanceRequest;
import com.example.spring.microservice.userservice.dto.request.UpdateUserBalanceRequest.BalanceOperation;
import com.example.spring.microservice.userservice.dto.request.UpdateUserRequest;
import com.example.spring.microservice.userservice.dto.request.UpdateUserStatusRequest;
import com.example.spring.microservice.userservice.dto.response.UserResponse;
import com.example.spring.microservice.userservice.dto.response.UserSummaryResponse;
import com.example.spring.microservice.userservice.exception.DuplicateEmailException;
import com.example.spring.microservice.userservice.exception.InsufficientBalanceException;
import com.example.spring.microservice.userservice.exception.UserNotFoundException;
import com.example.spring.microservice.userservice.model.User;
import com.example.spring.microservice.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // ── GET /users/me ────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public UserResponse getAuthenticatedUser(UUID userId) {
        return UserResponse.from(findUserById(userId));
    }

    // ── PUT /users/me ────────────────────────────────────────

    @Override
    @Transactional
    public UserResponse updateAuthenticatedUser(UUID userId, UpdateUserRequest request) {
        User user = findUserById(userId);

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEmailException(request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }

        return UserResponse.from(userRepository.save(user));
    }

    // ── GET /users/{userId} ──────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        return UserResponse.from(findUserById(userId));
    }

    // ── GET /users ───────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<UserSummaryResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserSummaryResponse::from);
    }

    // ── PATCH /users/{userId}/status ─────────────────────────

    @Override
    @Transactional
    public UserResponse updateUserStatus(UUID userId, UpdateUserStatusRequest request) {
        findUserById(userId);
        int updated = userRepository.updateAccountStatus(userId, request.getAccountStatus());
        if (updated == 0) {
            throw new UserNotFoundException(userId);
        }
        return UserResponse.from(findUserById(userId));
    }

    // ── PATCH /users/{userId}/balance ────────────────────────

    @Override
    @Transactional
    public UserResponse updateUserBalance(UUID userId, UpdateUserBalanceRequest request) {
        User user = findUserById(userId);
        int updated;

        if (request.getOperation() == BalanceOperation.DEBIT) {
            updated = userRepository.debitBalance(userId, request.getAmount());
            if (updated == 0) {
                throw new InsufficientBalanceException(userId, request.getAmount(), user.getBalance());
            }
        } else {
            updated = userRepository.creditBalance(userId, request.getAmount());
            if (updated == 0) {
                throw new UserNotFoundException(userId);
            }
        }

        return UserResponse.from(findUserById(userId));
    }

    // ── Existence check ──────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public boolean userExists(UUID userId) {
        return userRepository.existsByUserId(userId);
    }

    // ── Internal lookups ─────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByAccountNumber(String accountNumber) {
        User user = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UserNotFoundException("account: " + accountNumber));
        return UserResponse.from(user);
    }

    // ── Shared helper ────────────────────────────────────────

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}