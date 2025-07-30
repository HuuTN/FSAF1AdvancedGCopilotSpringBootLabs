package com.example.copilot.service.impl;

import com.example.copilot.core.dto.AuthResponseDTO;
import com.example.copilot.core.dto.LoginRequestDTO;
import com.example.copilot.core.dto.RegisterRequestDTO;
import com.example.copilot.core.entity.User;
import com.example.copilot.core.enums.UserRole;
import com.example.copilot.core.repository.UserRepository;
import com.example.copilot.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AuthResponseDTO register(RegisterRequestDTO request) {
        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        // Check if email already exists (SQL Injection safe with parameterized queries)
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        // Create user with encrypted password
        User user = new User();
        user.setName(sanitizeInput(request.getName()));
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER); // Default role
        user.setCreationDate(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        return new AuthResponseDTO("Registration successful", savedUser.getId(), null, savedUser.getRole().name());
    }
    
    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        String email = request.getEmail().toLowerCase().trim();
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        User user = userOpt.get();
        
        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        // For now, return simple response (can be enhanced with JWT later)
        return new AuthResponseDTO("Login successful", user.getId(), "simple-token", user.getRole().name());
    }
    
    /**
     * Sanitize input to prevent XSS
     */
    private String sanitizeInput(String input) {
        if (input == null) return null;
        return input.trim()
                   .replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;")
                   .replaceAll("/", "&#x2F;");
    }
}
