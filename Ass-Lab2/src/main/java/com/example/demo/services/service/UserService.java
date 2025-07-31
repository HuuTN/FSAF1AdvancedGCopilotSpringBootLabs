package com.example.demo.services.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.cores.dtos.UserDTO;

public interface UserService {
    UserDTO createUser(UserDTO UserDTO);
    Page<UserDTO> getAllUsers(Pageable pageable);
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, UserDTO UserDTO);
    void deleteUser(Long id);
}
