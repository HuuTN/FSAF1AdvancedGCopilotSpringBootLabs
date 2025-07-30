package com.example.copilot.exception;

public class UserNotPurchasedProductException extends RuntimeException {
    public UserNotPurchasedProductException(String message) {
        super(message);
    }
}
