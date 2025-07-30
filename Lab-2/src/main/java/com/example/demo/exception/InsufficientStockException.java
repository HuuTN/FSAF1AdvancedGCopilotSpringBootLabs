package com.example.demo.exception;

/**
 * Exception thrown when there is not enough stock for a product in an order request.
 * Should be used after checking each item in the request against the Product table stock.
 */
public class InsufficientStockException extends Exception {
    public InsufficientStockException(Long productId, int requested, int available) {
        super("Insufficient stock for product ID: " + productId + ". Requested: " + requested + ", Available: " + available);
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}
