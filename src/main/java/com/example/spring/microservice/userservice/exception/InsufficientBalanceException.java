package com.example.spring.microservice.userservice.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(UUID userId, BigDecimal requested, BigDecimal available) {
        super("Insufficient balance for user " + userId
                + ". Requested: " + requested
                + ", Available: " + available);
    }
}