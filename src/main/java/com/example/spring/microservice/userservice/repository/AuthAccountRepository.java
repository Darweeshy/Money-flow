package com.example.spring.microservice.userservice.repository;

import com.example.spring.microservice.userservice.model.AuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthAccountRepository extends JpaRepository<AuthAccount, UUID> {

    Optional<AuthAccount> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Modifying
    @Query("UPDATE AuthAccount a SET a.failedLoginCount = a.failedLoginCount + 1, " +
            "a.lastFailedAt = :lastFailedAt WHERE a.userId = :userId")
    int incrementFailedLoginCount(@Param("userId") UUID userId,
                                  @Param("lastFailedAt") OffsetDateTime lastFailedAt);

    @Modifying
    @Query("UPDATE AuthAccount a SET a.failedLoginCount = 0, " +
            "a.lastFailedAt = NULL WHERE a.userId = :userId")
    int resetFailedLoginCount(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE AuthAccount a SET a.tokenVersion = a.tokenVersion + 1 " +
            "WHERE a.userId = :userId")
    int incrementTokenVersion(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE AuthAccount a SET a.passwordHash = :passwordHash, " +
            "a.passwordChanged = true WHERE a.userId = :userId")
    int updatePasswordHash(@Param("userId") UUID userId,
                           @Param("passwordHash") String passwordHash);

    @Modifying
    @Query("UPDATE AuthAccount a SET a.passwordHash = :passwordHash, " +
            "a.passwordChanged = false WHERE a.userId = :userId")
    int updateTempPasswordHash(@Param("userId") UUID userId,
                               @Param("passwordHash") String passwordHash);
}