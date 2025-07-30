package com.example.copilot.core.constants;

/**
 * Application Constants for Review and Dashboard features
 * Centralizes magic numbers and string literals
 */
public final class ApplicationConstants {
    
    private ApplicationConstants() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Review System Constants
     */
    public static final class Review {
        public static final int MIN_RATING = 1;
        public static final int MAX_RATING = 5;
        public static final int MAX_COMMENT_LENGTH = 1000;
        public static final String DELIVERED_STATUS = "DELIVERED";
        
        private Review() {}
    }
    
    /**
     * Dashboard System Constants
     */
    public static final class Dashboard {
        public static final String ADMIN_ROLE = "ADMIN";
        public static final String MANAGER_ROLE = "MANAGER";
        
        private Dashboard() {}
    }
    
    /**
     * Database Constants
     */
    public static final class Database {
        public static final String UNIQUE_REVIEW_CONSTRAINT = "reviews.UK1nv3auyahyyy79hvtrcqgtfo9";
        
        private Database() {}
    }
    
    /**
     * Error Messages
     */
    public static final class ErrorMessages {
        public static final String USER_NOT_PURCHASED = "User has not purchased this product or order is not delivered yet.";
        public static final String DUPLICATE_REVIEW = "User has already reviewed this product.";
        public static final String PRODUCT_NOT_FOUND = "Product not found with id: ";
        public static final String USER_NOT_FOUND = "User not found with id: ";
        public static final String REVIEW_NOT_FOUND = "Review not found with id: ";
        
        private ErrorMessages() {}
    }
}
