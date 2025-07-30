package com.example.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
    
    public ReviewNotFoundException(Long reviewId) {
        super("Review not found with id: " + reviewId);
    }
    
    public ReviewNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
