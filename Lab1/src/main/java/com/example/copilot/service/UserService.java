package com.example.copilot.service;

import com.example.copilot.core.dto.UserDTO;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO create(UserDTO dto);
    Optional<UserDTO> update(Long id, UserDTO dto);
    boolean delete(Long id);
    Optional<UserDTO> getById(Long id);
    List<UserDTO> getAll();
}
