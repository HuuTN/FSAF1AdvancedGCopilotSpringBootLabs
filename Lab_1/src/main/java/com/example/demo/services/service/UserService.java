package com.example.demo.services.service;

import com.example.demo.core.dtos.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {
    UserDTO createUser(UserDTO userDto);
    Page<UserDTO> getAllUsers(Pageable pageable);
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, UserDTO userDto);
    void deleteUser(Long id);
}
