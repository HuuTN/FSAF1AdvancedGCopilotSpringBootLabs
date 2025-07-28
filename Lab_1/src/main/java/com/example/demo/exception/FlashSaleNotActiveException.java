package com.example.demo.exception;

public class FlashSaleNotActiveException extends RuntimeException {
    
    public FlashSaleNotActiveException(String message) {
        super(message);
    }
    
    public FlashSaleNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
