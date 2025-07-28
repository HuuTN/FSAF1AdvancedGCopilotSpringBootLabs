// Service interface for User with create, getAll, update, delete methods
package com.example.service;
import com.example.dto.UserDTO;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    List<UserDTO> getAllUsers(); // Get all users
    Page<UserDTO> getAllUsers(Pageable pageable); // Get all users with pagination

    UserDTO getUserById(Long id); // Get user by ID

    UserDTO createUser(UserDTO userDTO); // Create a new user

    UserDTO updateUser(Long id, UserDTO userDTO); // Update an existing user

    void deleteUser(Long id); // Delete a user by ID
}
