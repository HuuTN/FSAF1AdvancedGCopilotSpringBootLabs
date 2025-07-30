package com.example.copilot.exception;

/**
 * Exception thrown when a user tries to review a product they have already reviewed
 */
public class DuplicateReviewException extends RuntimeException {
    
    public DuplicateReviewException(String message) {
        super(message);
    }
    
    public DuplicateReviewException(String message, Throwable cause) {
        super(message, cause);
    }
}
