package com.example.model.enums;

/**
 * Enum representing user roles in the ecommerce platform.
 */
public enum UserRole {
    ADMIN("Admin"),
    MANAGER("Manager"),
    EMPLOYEE("Employee"),
    CUSTOMER_SERVICE("Customer Service");

    private final String displayName;

    UserRole(String displayName) {
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
