package com.example.demo.exception;

public class UserNotPurchasedProductException extends RuntimeException {
    public UserNotPurchasedProductException(String message) {
        super(message);
    }
    
    public UserNotPurchasedProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
