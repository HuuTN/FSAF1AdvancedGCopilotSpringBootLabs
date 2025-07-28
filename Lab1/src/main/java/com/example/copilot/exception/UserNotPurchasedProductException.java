package com.example.copilot.exception;

/**
 * Exception thrown when a user tries to review a product they haven't purchased
 */
public class UserNotPurchasedProductException extends RuntimeException {
    
    public UserNotPurchasedProductException(String message) {
        super(message);
    }
    
    public UserNotPurchasedProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
