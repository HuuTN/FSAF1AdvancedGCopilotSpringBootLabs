package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;

public interface UserService {
    User addUser(User user);

    User getUserById(Long userId);

    void updateUser(User user);

    void deleteUser(Long userId);

    List<UserDTO> getAllUsers();
    
    List<User> listAll();

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Long getUserIdByUsername(String username);
}
