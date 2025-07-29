package com.example.exception;

/**
 * Custom exception thrown when a customer tries to create a duplicate review
 * for a product they have already reviewed
 */
public class DuplicateReviewException extends RuntimeException {

    private final Long customerId;
    private final Long productId;

    public DuplicateReviewException(Long customerId, Long productId) {
        super(String.format(
                "Customer with ID %d has already reviewed product with ID %d",
                customerId, productId));
        this.customerId = customerId;
        this.productId = productId;
    }

    public DuplicateReviewException(String message) {
        super(message);
        this.customerId = null;
        this.productId = null;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getProductId() {
        return productId;
    }
}
