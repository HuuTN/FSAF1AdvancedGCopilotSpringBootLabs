package com.example.copilot.service.impl;

import com.example.copilot.core.dto.UserDTO;
import com.example.copilot.core.entity.User;
import com.example.copilot.core.repository.UserRepository;
import com.example.copilot.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        return dto;
    }

    private User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        if (dto.getRole() != null) {
            user.setRole(com.example.copilot.core.enums.UserRole.valueOf(dto.getRole()));
        } else {
            user.setRole(null);
        }
        return user;
    }

    @Override
    public UserDTO create(UserDTO dto) {
        User user = toEntity(dto);
        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    @Override
    public Optional<UserDTO> update(Long id, UserDTO dto) {
        if (!userRepository.existsById(id)) return Optional.empty();
        User user = toEntity(dto);
        user.setId(id);
        User saved = userRepository.save(user);
        return Optional.of(toDTO(saved));
    }

    @Override
    public boolean delete(Long id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public Optional<UserDTO> getById(Long id) {
        return userRepository.findById(id).map(this::toDTO);
    }

    @Override
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }
}
