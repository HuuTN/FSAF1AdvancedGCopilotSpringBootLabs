package com.example.demo.services.serviceImpl;

import com.example.demo.services.service.UserSecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityServiceImpl implements UserSecurityService {
    
    @Override
    public Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }
        
        // For demo purposes, map usernames to user IDs
        // In production, this would come from a User entity or JWT token
        String username = authentication.getName();
        switch (username) {
            case "admin": return 1L;
            case "manager": return 2L;
            case "user1": return 3L;
            case "user2": return 4L;
            default: return Long.valueOf(username.hashCode() % 1000 + 1);
        }
    }
    
    @Override
    public String getCurrentUsername(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }
        return authentication.getName();
    }
    
    @Override
    public boolean hasRole(Authentication authentication, String role) {
        if (authentication == null) {
            return false;
        }
        
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_" + role));
    }
}
