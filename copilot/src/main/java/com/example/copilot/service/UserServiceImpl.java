package com.example.copilot.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.copilot.dto.UserDTO;
import com.example.copilot.entity.User;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDTO addUser(UserDTO userDTO) {
        User user = toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        return userOpt.map(this::toDTO).orElse(null);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(userDTO.getName());
            user.setEmail(userDTO.getEmail());
            user.setRole(userDTO.getRole());
            User updatedUser = userRepository.save(user);
            return toDTO(updatedUser);
        }
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Helper methods for mapping
    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    private User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setPassword(""); // Set password as needed
        return user;
    }
}
