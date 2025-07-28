package com.example.copilot.service;

import java.util.List;

import com.example.copilot.dto.UserDTO;

public interface UserService {
    UserDTO addUser(UserDTO userDTO);
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    List<UserDTO> getAllUsers();
}
