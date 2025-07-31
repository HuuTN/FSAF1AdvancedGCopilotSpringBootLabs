package com.example.demo.services.service;

import org.springframework.security.core.Authentication;

public interface UserSecurityService {
    
    /**
     * Get the current authenticated user's ID
     */
    Long getCurrentUserId(Authentication authentication);
    
    /**
     * Get the current authenticated user's username
     */
    String getCurrentUsername(Authentication authentication);
    
    /**
     * Check if the current user has a specific role
     */
    boolean hasRole(Authentication authentication, String role);
}
