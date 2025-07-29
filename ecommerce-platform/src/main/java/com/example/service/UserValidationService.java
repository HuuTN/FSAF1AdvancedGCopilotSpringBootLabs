package com.example.service;

import com.example.model.dto.UserDTO;

/**
 * Service interface for user validation operations.
 * Handles input validation, sanitization, and business rule validation.
 */
public interface UserValidationService {

    /**
     * Validates user registration input including password complexity,
     * email format, name sanitization, and role permissions.
     * 
     * @param userDTO the user data to validate
     * @throws IllegalArgumentException if validation fails
     */
    void validateUserRegistrationInput(UserDTO userDTO);

    /**
     * Validates password complexity requirements.
     * 
     * @param password the password to validate
     * @return true if password meets complexity requirements
     */
    boolean isPasswordComplexEnough(String password);

    /**
     * Sanitizes and normalizes user input data.
     * 
     * @param userDTO the user data to sanitize
     */
    void sanitizeUserInput(UserDTO userDTO);

    /**
     * Validates role assignment permissions.
     * 
     * @param userDTO the user data with role to validate
     * @throws IllegalArgumentException if role assignment is not allowed
     */
    void validateRoleAssignment(UserDTO userDTO);
}
