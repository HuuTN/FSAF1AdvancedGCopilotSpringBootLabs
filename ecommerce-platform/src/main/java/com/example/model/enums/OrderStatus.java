package com.example.model.enums;

/**
 * Enum representing order statuses in the ecommerce platform.
 */
public enum OrderStatus {
    PENDING("Pending"),
    CANCELED("Canceled"),
    CONFIRMED("Confirmed"),
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    COMPLETED("Completed");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
