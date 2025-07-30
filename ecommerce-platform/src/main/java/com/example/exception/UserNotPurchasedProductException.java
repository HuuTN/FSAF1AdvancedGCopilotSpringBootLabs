package com.example.exception;

/**
 * Custom exception thrown when a user tries to review a product they have not
 * purchased
 */
public class UserNotPurchasedProductException extends RuntimeException {

    private final Long customerId;
    private final Long productId;

    public UserNotPurchasedProductException(Long customerId, Long productId) {
        super(String.format(
                "Customer with ID %d has not purchased product with ID %d or has no delivered orders for this product",
                customerId, productId));
        this.customerId = customerId;
        this.productId = productId;
    }

    public UserNotPurchasedProductException(String message) {
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
