package com.example.copilot.service;

import com.example.copilot.core.dto.AuthResponseDTO;
import com.example.copilot.core.dto.LoginRequestDTO;
import com.example.copilot.core.dto.RegisterRequestDTO;

public interface AuthService {
    
    /**
     * Register a new user
     * @param request Registration request with user details
     * @return Authentication response with user info
     */
    AuthResponseDTO register(RegisterRequestDTO request);
    
    /**
     * Authenticate user login
     * @param request Login request with credentials
     * @return Authentication response with token and user info
     */
    AuthResponseDTO login(LoginRequestDTO request);
}
