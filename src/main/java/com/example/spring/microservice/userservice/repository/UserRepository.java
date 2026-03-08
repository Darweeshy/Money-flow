package com.example.spring.microservice.userservice.repository;

import com.example.spring.microservice.userservice.enums.AccountStatus;
import com.example.spring.microservice.userservice.enums.Role;
import com.example.spring.microservice.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // ── Single user lookups ──────────────────────────────────

    // Used by Auth Service on every login to find user by email
    Optional<User> findByEmail(String email);

    // Check if an email is already taken before updating
    boolean existsByEmail(String email);

    // Check if a user exists by ID (used by Transfer Service to verify recipient)
    boolean existsByUserId(UUID userId);

    Optional<User> findByAccountNumber(String accountNumber);

    // ── Admin queries ────────────────────────────────────────

    // Paginated full user list for admin dashboard
    Page<User> findAll(Pageable pageable);

    // Filter by account status (e.g. show all LOCKED accounts)
    Page<User> findByAccountStatus(AccountStatus accountStatus, Pageable pageable);

    // Filter by role (e.g. show all admins)
    Page<User> findByRole(Role role, Pageable pageable);

    // Filter by both role and status
    Page<User> findByRoleAndAccountStatus(Role role, AccountStatus accountStatus, Pageable pageable);

    // ── Balance operations ───────────────────────────────────

    // Credit — add amount to balance
    @Modifying
    @Query("UPDATE User u SET u.balance = u.balance + :amount WHERE u.userId = :userId AND u.balance + :amount >= 0")
    int creditBalance(@Param("userId") UUID userId, @Param("amount") BigDecimal amount);

    // Debit — subtract amount from balance (guard: balance must be >= amount)
    @Modifying
    @Query("UPDATE User u SET u.balance = u.balance - :amount WHERE u.userId = :userId AND u.balance >= :amount")
    int debitBalance(@Param("userId") UUID userId, @Param("amount") BigDecimal amount);

    // ── Status update ────────────────────────────────────────

    // Update account status (called by admin endpoints)
    @Modifying
    @Query("UPDATE User u SET u.accountStatus = :status WHERE u.userId = :userId")
    int updateAccountStatus(@Param("userId") UUID userId, @Param("status") AccountStatus status);

    // Update email address (called by updateAuthenticatedUser)
    @Modifying
    @Query("UPDATE User u SET u.email = :email WHERE u.userId = :userId")
    int updateEmail(@Param("userId") UUID userId, @Param("email") String email);
}