package com.example.spring.microservice.userservice.repository;

import com.example.spring.microservice.userservice.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    // Find token by hash — used on every refresh request
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // Find all active tokens for a user — used for global logout
    @Query("SELECT r FROM RefreshToken r WHERE r.userId = :userId AND r.revoked = false " +
            "AND r.expiresAt > :now")
    java.util.List<RefreshToken> findActiveTokensByUserId(@Param("userId") UUID userId,
                                                          @Param("now") OffsetDateTime now);

    // Revoke a single token by hash
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.tokenHash = :tokenHash")
    int revokeByTokenHash(@Param("tokenHash") String tokenHash);

    // Revoke all tokens for a user — global logout
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.userId = :userId")
    int revokeAllByUserId(@Param("userId") UUID userId);

    // Delete expired and revoked tokens — for cleanup job
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < :now OR r.revoked = true")
    int deleteExpiredAndRevoked(@Param("now") OffsetDateTime now);
}