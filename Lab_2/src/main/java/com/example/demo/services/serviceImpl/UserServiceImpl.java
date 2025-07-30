package com.example.demo.services.serviceImpl;

import com.example.demo.core.entity.User;
import com.example.demo.core.dtos.UserDTO;
import com.example.demo.core.repository.UserRepository;
import com.example.demo.services.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Convert User entity to UserDTO
     */
    private UserDTO toDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User(userDTO.getName(), userDTO.getEmail());
        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(userDTOs, pageable, userPage.getTotalElements());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        return toDTO(user);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        User updated = userRepository.save(user);
        return toDTO(updated);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }
}
