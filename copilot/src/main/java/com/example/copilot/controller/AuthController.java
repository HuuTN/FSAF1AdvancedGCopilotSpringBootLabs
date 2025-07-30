package com.example.copilot.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest request) {
        // Registration logic here
        return ResponseEntity.ok("User registered successfully");
    }

    // DTO for registration
    public static class RegistrationRequest {
        @javax.validation.constraints.NotBlank
        private String username;
        @javax.validation.constraints.NotBlank
        private String password;
        @javax.validation.constraints.Email
        private String email;
        // getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
