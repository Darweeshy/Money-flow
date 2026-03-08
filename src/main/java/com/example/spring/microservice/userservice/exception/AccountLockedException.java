package com.example.spring.microservice.userservice.exception;

public class AccountLockedException extends RuntimeException {

    public AccountLockedException() {
        super("Account is locked. Please contact support to unlock your account");
    }
}