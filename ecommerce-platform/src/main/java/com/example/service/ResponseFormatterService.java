package com.example.service;

import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * Service interface for creating standardized API responses.
 * Handles response formatting and error response creation.
 */
public interface ResponseFormatterService {

    /**
     * Creates a success response with data.
     * 
     * @param message the success message
     * @param data    the response data
     * @return formatted success response
     */
    ResponseEntity<Map<String, Object>> createSuccessResponse(String message, Object data);

    /**
     * Creates an error response.
     * 
     * @param error      the error type
     * @param message    the error message
     * @param statusCode the HTTP status code
     * @return formatted error response
     */
    ResponseEntity<Map<String, Object>> createErrorResponse(String error, String message, int statusCode);

    /**
     * Creates a registration success response.
     * 
     * @param user the created user data (without password)
     * @return formatted registration response
     */
    ResponseEntity<Map<String, Object>> createRegistrationSuccessResponse(Object user);
}
