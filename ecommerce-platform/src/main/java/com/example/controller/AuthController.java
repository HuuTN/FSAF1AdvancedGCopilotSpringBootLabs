package com.example.controller;

import com.example.model.dto.UserDTO;
import com.example.model.enums.UserRole;
import com.example.service.UserService;
import com.example.service.UserValidationService;
import com.example.service.ResponseFormatterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller for the eCommerce platform.
 * Handles user authentication, login, logout, and user profile operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication", description = "Authentication and authorization operations")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserValidationService userValidationService;

    @Autowired
    private ResponseFormatterService responseFormatterService;

    /**
     * Login endpoint for Basic Authentication
     * Since Spring Security handles Basic Auth automatically, this endpoint
     * validates credentials and returns user information upon successful
     * authentication
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with username and password using Basic Auth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid request format")
    })
    public ResponseEntity<?> login(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Check if Authorization header is present
            if (authHeader == null || authHeader.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Missing Authorization header");
                errorResponse.put("message", "Authorization header with Basic authentication is required");
                errorResponse.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Extract credentials from Basic Auth header
            String[] credentials = extractCredentials(authHeader);
            String username = credentials[0];
            String password = credentials[1];

            // Validate credentials
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("Invalid credentials");
            }

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("username", username);
            response.put("authorities", userDetails.getAuthorities());
            response.put("timestamp", System.currentTimeMillis());

            log.info("User {} logged in successfully", username);
            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException | BadCredentialsException e) {
            log.warn("Login failed: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid credentials");
            errorResponse.put("message", "Username or password is incorrect");
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication failed");
            errorResponse.put("message", "An error occurred during authentication");
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get current authenticated user information
     */
    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get current authenticated user's profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<?> getCurrentUserProfile(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            String username = principal.getName();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            Map<String, Object> profile = new HashMap<>();
            profile.put("username", username);
            profile.put("authorities", userDetails.getAuthorities());
            profile.put("accountNonExpired", userDetails.isAccountNonExpired());
            profile.put("accountNonLocked", userDetails.isAccountNonLocked());
            profile.put("credentialsNonExpired", userDetails.isCredentialsNonExpired());
            profile.put("enabled", userDetails.isEnabled());

            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            log.error("Error retrieving user profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve profile"));
        }
    }

    /**
     * Register a new user (for internal system users)
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new system user (internal use)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            // Delegate validation to UserValidationService
            userValidationService.validateUserRegistrationInput(userDTO);

            // Encode password before saving
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

            // Set default role if not specified
            if (userDTO.getRole() == null) {
                userDTO.setRole(UserRole.EMPLOYEE);
            }

            UserDTO createdUser = userService.createUser(userDTO);

            // Remove password from response
            createdUser.setPassword(null);

            log.info("New user registered: {}", createdUser.getEmail());
            return responseFormatterService.createRegistrationSuccessResponse(createdUser);

        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            return responseFormatterService.createErrorResponse(
                    "Registration failed", e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout current authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null) {
                String username = auth.getName();
                new SecurityContextLogoutHandler().logout(request, response, auth);

                Map<String, Object> logoutResponse = new HashMap<>();
                logoutResponse.put("message", "Logout successful");
                logoutResponse.put("username", username);
                logoutResponse.put("timestamp", System.currentTimeMillis());

                log.info("User {} logged out successfully", username);
                return ResponseEntity.ok(logoutResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

        } catch (Exception e) {
            log.error("Logout error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Logout failed"));
        }
    }

    /**
     * Change password endpoint
     */
    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password for current authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> passwordData,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Current password and new password are required"));
            }

            if (newPassword.length() < 8) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "New password must be at least 8 characters long"));
            }

            // Validate password complexity using the validation service
            if (!userValidationService.isPasswordComplexEnough(newPassword)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "New password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"));
            }

            String username = principal.getName();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, userDetails.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Current password is incorrect"));
            }

            // Note: In a real application, you would update the password in the database
            // Since we're using in-memory authentication, we'll just return success
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Password change successful");
            response.put("username", username);
            response.put("timestamp", System.currentTimeMillis());

            log.info("Password changed for user: {}", username);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Password change error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Password change failed"));
        }
    }

    /**
     * Check authentication status
     */
    @GetMapping("/status")
    @Operation(summary = "Check authentication status", description = "Check if user is currently authenticated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication status retrieved")
    })
    public ResponseEntity<?> getAuthStatus(Principal principal) {
        Map<String, Object> status = new HashMap<>();

        if (principal != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            status.put("authenticated", true);
            status.put("username", principal.getName());
            status.put("authorities", auth.getAuthorities());
        } else {
            status.put("authenticated", false);
            status.put("username", null);
            status.put("authorities", null);
        }

        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }

    /**
     * Extract username and password from Basic Auth header
     */
    private String[] extractCredentials(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new BadCredentialsException("Invalid Authorization header");
        }

        String base64Credentials = authHeader.substring("Basic ".length());
        byte[] decodedCredentials = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedCredentials, StandardCharsets.UTF_8);

        String[] parts = credentials.split(":", 2);
        if (parts.length != 2) {
            throw new BadCredentialsException("Invalid credentials format");
        }

        return parts;
    }
}
