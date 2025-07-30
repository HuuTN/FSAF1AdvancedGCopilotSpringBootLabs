package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import java.util.List;

public interface UserService {
    UserDTO addUser(UserDTO userDTO);
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    List<UserDTO> getAllUsers();
}
