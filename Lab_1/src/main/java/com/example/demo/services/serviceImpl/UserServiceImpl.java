package com.example.demo.services.serviceImpl;

import com.example.demo.core.entity.User;
import com.example.demo.core.dtos.UserDTO;
import com.example.demo.core.repository.UserRepository;

import org.springframework.stereotype.Service;
import com.example.demo.services.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    // @Autowired removed
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User(1L, "John Doe", "john@example.com");
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        User saved = userRepository.save(user);
        return new UserDTO(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        org.springframework.data.domain.Page<User> userPage = userRepository.findAll(pageable);
        java.util.List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole()))
                .collect(java.util.stream.Collectors.toList());
        return new PageImpl<>(userDTOs, pageable, userPage.getTotalElements());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User with id " + id + " not found"));
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User with id " + id + " not found"));
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        User updated = userRepository.save(user);
        return new UserDTO(updated.getId(), updated.getName(), updated.getEmail(), updated.getRole());
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }
}
