package com.example.demo.exception;

public class FlashSaleSoldOutException extends RuntimeException {
    
    public FlashSaleSoldOutException(String message) {
        super(message);
    }
    
    public FlashSaleSoldOutException(String message, Throwable cause) {
        super(message, cause);
    }
}
