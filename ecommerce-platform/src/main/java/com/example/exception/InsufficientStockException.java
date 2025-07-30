package com.example.exception;

/**
 * Custom exception thrown when there is insufficient stock for a product
 */
public class InsufficientStockException extends RuntimeException {

    private final String productName;
    private final int availableStock;
    private final int requestedQuantity;

    public InsufficientStockException(String productName, int availableStock, int requestedQuantity) {
        super(String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
                productName, availableStock, requestedQuantity));
        this.productName = productName;
        this.availableStock = availableStock;
        this.requestedQuantity = requestedQuantity;
    }

    public InsufficientStockException(String message) {
        super(message);
        this.productName = null;
        this.availableStock = 0;
        this.requestedQuantity = 0;
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
        this.productName = null;
        this.availableStock = 0;
        this.requestedQuantity = 0;
    }

    public String getProductName() {
        return productName;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }
}
