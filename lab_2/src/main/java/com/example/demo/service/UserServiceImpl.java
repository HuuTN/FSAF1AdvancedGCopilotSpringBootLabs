package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    private User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        // Password cần xử lý riêng khi tạo mới hoặc cập nhật
        return user;
    }

    @Override
    public UserDTO addUser(UserDTO userDTO) {
        User user = toEntity(userDTO);
        // Xử lý password ở đây nếu cần
        User saved = userRepository.save(user);
        return toDTO(saved);
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
            // Password cần xử lý riêng nếu cập nhật
            User updated = userRepository.save(user);
            return toDTO(updated);
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
}
