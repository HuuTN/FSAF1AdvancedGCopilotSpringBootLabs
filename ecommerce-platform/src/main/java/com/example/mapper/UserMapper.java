package com.example.mapper;

import org.springframework.stereotype.Component;

import com.example.model.dto.UserDTO;
import com.example.model.entity.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between User entity and UserDTO
 */
@Component
public class UserMapper {

    /**
     * Convert User entity to UserDTO
     * 
     * @param user The user entity to convert
     * @return UserDTO representation
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password("********") // Masked password for security
                .role(user.getRole())
                .build();
    }

    /**
     * Convert UserDTO to User entity
     * 
     * @param userDTO The UserDTO to convert
     * @return User entity
     */
    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        return user;
    }

    /**
     * Update existing User entity with UserDTO data
     * 
     * @param existingUser The existing user entity
     * @param userDTO      The DTO with updated data
     * @return Updated user entity
     */
    public User updateEntity(User existingUser, UserDTO userDTO) {
        if (existingUser == null || userDTO == null) {
            return existingUser;
        }

        existingUser.setName(userDTO.getName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setRole(userDTO.getRole());

        // Only update password if it's not the masked value
        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()
                && !userDTO.getPassword().equals("********")) {
            existingUser.setPassword(userDTO.getPassword());
        }

        return existingUser;
    }

    /**
     * Convert list of User entities to list of UserDTOs
     * 
     * @param users The list of users to convert
     * @return List of UserDTO representations
     */
    public List<UserDTO> toDTOs(List<User> users) {
        if (users == null) {
            return null;
        }

        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
