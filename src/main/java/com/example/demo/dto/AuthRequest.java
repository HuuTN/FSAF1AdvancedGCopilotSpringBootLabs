package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

public class AuthRequest {
    @NotNull(message = "Email không được để trống")
    private String email;

    @NotNull(message = "Mật khẩu không được để trống")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
