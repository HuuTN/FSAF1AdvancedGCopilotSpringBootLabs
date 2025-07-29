package com.example.service.serviceimpl;

import com.example.service.ResponseFormatterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of ResponseFormatterService.
 * Provides standardized response formatting for the API.
 */
@Service
public class ResponseFormatterServiceImpl implements ResponseFormatterService {

    @Override
    public ResponseEntity<Map<String, Object>> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> createErrorResponse(String error, String message, int statusCode) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(statusCode).body(errorResponse);
    }

    @Override
    public ResponseEntity<Map<String, Object>> createRegistrationSuccessResponse(Object user) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("user", user);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
