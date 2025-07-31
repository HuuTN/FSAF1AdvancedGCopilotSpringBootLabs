package com.example.usermanagement.exception;

public class UserNotPurchasedProductException extends RuntimeException {
    public UserNotPurchasedProductException(String message) {
        super(message);
    }
} 