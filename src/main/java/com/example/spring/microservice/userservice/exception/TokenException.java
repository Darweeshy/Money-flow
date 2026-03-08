package com.example.spring.microservice.userservice.exception;
public class TokenException extends RuntimeException {

    public TokenException(String message) {
        super(message);
    }

    public static TokenException invalid() {
        return new TokenException("Token is invalid or has expired");
    }

    public static TokenException revoked() {
        return new TokenException("Token has been revoked");
    }

    public static TokenException expired() {
        return new TokenException("Token has expired");
    }
}
