package com.example.service.serviceimpl;

import com.example.model.dto.UserDTO;
import com.example.model.enums.UserRole;
import com.example.service.UserValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserValidationService.
 * Handles all user input validation, sanitization, and business rule
 * enforcement.
 */
@Slf4j
@Service
public class UserValidationServiceImpl implements UserValidationService {

    // Configuration constants
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 100;
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    @Override
    public void validateUserRegistrationInput(UserDTO userDTO) {
        log.debug("Validating user registration input for email: {}", userDTO.getEmail());

        // Sanitize input first
        sanitizeUserInput(userDTO);

        // Validate password complexity
        if (!isPasswordComplexEnough(userDTO.getPassword())) {
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
        }

        // Validate role assignment
        validateRoleAssignment(userDTO);

        log.debug("User registration input validation completed successfully");
    }

    @Override
    public boolean isPasswordComplexEnough(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            return false;
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> SPECIAL_CHARACTERS.indexOf(ch) >= 0);

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    @Override
    public void sanitizeUserInput(UserDTO userDTO) {
        // Normalize email to lowercase
        if (userDTO.getEmail() != null) {
            userDTO.setEmail(userDTO.getEmail().toLowerCase().trim());
        }

        // Sanitize name field
        if (userDTO.getName() != null) {
            userDTO.setName(userDTO.getName().trim());
            if (userDTO.getName().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty after trimming whitespace");
            }
        }

        log.debug("User input sanitization completed");
    }

    @Override
    public void validateRoleAssignment(UserDTO userDTO) {
        // Prevent direct admin role assignment through registration
        if (userDTO.getRole() == UserRole.ADMIN) {
            throw new IllegalArgumentException("Admin role cannot be assigned through registration");
        }

        log.debug("Role assignment validation completed for role: {}", userDTO.getRole());
    }
}
