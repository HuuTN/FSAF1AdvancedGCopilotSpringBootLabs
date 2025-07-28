package com.example.demo.exception;

public class FlashSaleNotFoundException extends RuntimeException {
    
    public FlashSaleNotFoundException(String message) {
        super(message);
    }
    
    public FlashSaleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
