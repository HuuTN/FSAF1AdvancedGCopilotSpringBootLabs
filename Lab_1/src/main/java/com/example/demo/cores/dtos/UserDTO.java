package com.example.demo.cores.dtos;

// Updated: User entity is now in com.example.demo.entity
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import com.example.demo.cores.enums.UserRole;
public class UserDTO {
    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;

    @NotNull(message = "User role is mandatory")
    private UserRole userRole;

    public UserDTO() {}

    public UserDTO(String name, String email, UserRole userRole) {
        this.name = name;
        this.email = email;
        this.userRole = userRole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
