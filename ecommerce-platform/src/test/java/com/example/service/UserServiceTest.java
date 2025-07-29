package com.example.service;

import com.example.exception.ResourceNotFoundException;
import com.example.exception.ServiceException;
import com.example.model.dto.UserDTO;
import com.example.model.entity.User;
import com.example.model.enums.UserRole;
import com.example.repository.UserRepository;
import com.example.service.serviceimpl.UserServiceImpl;
import com.example.mapper.UserMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private UserMapper userMapper;

        @InjectMocks
        private UserServiceImpl userService;

        private User testUser;
        private UserDTO testUserDTO;
        private List<User> userList;

        @BeforeEach
        void setUp() {
                // Setup test user entity
                testUser = User.builder()
                                .id(1L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .password("password123")
                                .role(UserRole.EMPLOYEE)
                                .build();

                // Setup test user DTO
                testUserDTO = UserDTO.builder()
                                .id(1L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .password("password123")
                                .role(UserRole.EMPLOYEE)
                                .build();

                // Setup user list for testing
                User user2 = User.builder()
                                .id(2L)
                                .name("Jane Smith")
                                .email("jane.smith@example.com")
                                .password("password456")
                                .role(UserRole.ADMIN)
                                .build();

                userList = Arrays.asList(testUser, user2);
        }

        @Test
        void getAllUsers_ShouldReturnListOfUserDTOs() {
                // Given
                when(userRepository.findAll()).thenReturn(userList);
                when(userMapper.toDTO(any(User.class))).thenAnswer(invocation -> {
                        User user = invocation.getArgument(0);
                        return UserDTO.builder()
                                        .id(user.getId())
                                        .name(user.getName())
                                        .email(user.getEmail())
                                        .password("********")
                                        .role(user.getRole())
                                        .build();
                });

                // When
                List<UserDTO> result = userService.getAllUsers();

                // Then
                assertNotNull(result);
                assertEquals(2, result.size());
                assertEquals("John Doe", result.get(0).getName());
                assertEquals("Jane Smith", result.get(1).getName());
                assertEquals("********", result.get(0).getPassword()); // Password should be masked
                assertEquals("********", result.get(1).getPassword()); // Password should be masked

                verify(userRepository, times(1)).findAll();
                verify(userMapper, times(2)).toDTO(any(User.class));
        }

        @Test
        void getAllUsersWithPageable_ShouldReturnPageOfUserDTOs() {
                // Given
                Pageable pageable = PageRequest.of(0, 10);
                Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());
                when(userRepository.findAll(pageable)).thenReturn(userPage);
                when(userMapper.toDTO(any(User.class))).thenAnswer(invocation -> {
                        User user = invocation.getArgument(0);
                        return UserDTO.builder()
                                        .id(user.getId())
                                        .name(user.getName())
                                        .email(user.getEmail())
                                        .password("********")
                                        .role(user.getRole())
                                        .build();
                });

                // When
                Page<UserDTO> result = userService.getAllUsers(pageable);

                // Then
                assertNotNull(result);
                assertEquals(2, result.getContent().size());
                assertEquals("John Doe", result.getContent().get(0).getName());
                assertEquals("********", result.getContent().get(0).getPassword()); // Password should be masked

                verify(userRepository, times(1)).findAll(pageable);
                verify(userMapper, times(2)).toDTO(any(User.class));
        }

        @Test
        void getUserById_WithValidId_ShouldReturnUserDTO() {
                // Given
                UserDTO expectedDTO = UserDTO.builder()
                                .id(1L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .password("********")
                                .role(UserRole.EMPLOYEE)
                                .build();

                when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
                when(userMapper.toDTO(testUser)).thenReturn(expectedDTO);

                // When
                UserDTO result = userService.getUserById(1L);

                // Then
                assertNotNull(result);
                assertEquals(1L, result.getId());
                assertEquals("John Doe", result.getName());
                assertEquals("john.doe@example.com", result.getEmail());
                assertEquals("********", result.getPassword()); // Password should be masked
                assertEquals(UserRole.EMPLOYEE, result.getRole());

                verify(userRepository, times(1)).findById(1L);
                verify(userMapper, times(1)).toDTO(testUser);
        }

        @Test
        void getUserById_WithInvalidId_ShouldThrowResourceNotFoundException() {
                // Given
                when(userRepository.findById(999L)).thenReturn(Optional.empty());

                // When & Then
                ResourceNotFoundException exception = assertThrows(
                                ResourceNotFoundException.class,
                                () -> userService.getUserById(999L));

                assertEquals("User not found with id: 999", exception.getMessage());
                verify(userRepository, times(1)).findById(999L);
        }

        @Test
        void createUser_WithValidUserDTO_ShouldReturnCreatedUserDTO() {
                // Given
                User userToSave = User.builder()
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .password("password123")
                                .role(UserRole.EMPLOYEE)
                                .build();

                User savedUser = User.builder()
                                .id(1L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .password("password123")
                                .role(UserRole.EMPLOYEE)
                                .build();

                UserDTO expectedDTO = UserDTO.builder()
                                .id(1L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .password("********")
                                .role(UserRole.EMPLOYEE)
                                .build();

                when(userMapper.toEntity(any(UserDTO.class))).thenReturn(userToSave);
                when(userRepository.save(any(User.class))).thenReturn(savedUser);
                when(userMapper.toDTO(savedUser)).thenReturn(expectedDTO);

                // When
                UserDTO result = userService.createUser(testUserDTO);

                // Then
                assertNotNull(result);
                assertEquals(1L, result.getId());
                assertEquals("John Doe", result.getName());
                assertEquals("john.doe@example.com", result.getEmail());
                assertEquals("********", result.getPassword()); // Password should be masked in response
                assertEquals(UserRole.EMPLOYEE, result.getRole());

                verify(userMapper, times(1)).toEntity(any(UserDTO.class));
                verify(userRepository, times(1)).save(any(User.class));
                verify(userMapper, times(1)).toDTO(any(User.class));
        }

        @Test
        void createUser_WithRepositoryException_ShouldPropagateException() {
                // Given
                User userEntity = User.builder()
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .password("password123")
                                .role(UserRole.EMPLOYEE)
                                .build();

                when(userMapper.toEntity(eq(testUserDTO))).thenReturn(userEntity);
                when(userRepository.save(eq(userEntity))).thenThrow(new RuntimeException("Database error"));

                // When & Then
                ServiceException exception = assertThrows(
                                ServiceException.class,
                                () -> userService.createUser(testUserDTO));

                assertEquals("Error creating user", exception.getMessage());
                assertEquals("Database error", exception.getCause().getMessage());
                verify(userMapper, times(1)).toEntity(eq(testUserDTO));
                verify(userRepository, times(1)).save(eq(userEntity));
        }

        @Test
        void updateUser_WithValidIdAndUserDTO_ShouldReturnUpdatedUserDTO() {
                // Given
                UserDTO updateDTO = UserDTO.builder()
                                .name("John Doe Updated")
                                .email("john.doe.updated@example.com")
                                .password("newPassword123")
                                .role(UserRole.MANAGER)
                                .build();

                // Create a copy of testUser that will be modified
                User userToUpdate = User.builder()
                                .id(1L)
                                .name("John Doe")
                                .email("john.doe@example.com")
                                .password("password123")
                                .role(UserRole.EMPLOYEE)
                                .build();

                User updatedUser = User.builder()
                                .id(1L)
                                .name("John Doe Updated")
                                .email("john.doe.updated@example.com")
                                .password("password123")
                                .role(UserRole.EMPLOYEE)
                                .build();

                UserDTO expectedDTO = UserDTO.builder()
                                .id(1L)
                                .name("John Doe Updated")
                                .email("john.doe.updated@example.com")
                                .password("********")
                                .role(UserRole.EMPLOYEE)
                                .build();

                when(userRepository.findById(1L)).thenReturn(Optional.of(userToUpdate));
                when(userMapper.updateEntity(any(User.class), any(UserDTO.class))).thenReturn(updatedUser);
                when(userRepository.save(any(User.class))).thenReturn(updatedUser);
                when(userMapper.toDTO(any(User.class))).thenReturn(expectedDTO);

                // When
                UserDTO result = userService.updateUser(1L, updateDTO);

                // Then
                assertNotNull(result);
                assertEquals(1L, result.getId());
                assertEquals("John Doe Updated", result.getName());
                assertEquals("john.doe.updated@example.com", result.getEmail());
                assertEquals("********", result.getPassword()); // Password should be masked
                assertEquals(UserRole.EMPLOYEE, result.getRole()); // Role remains unchanged in current implementation

                verify(userRepository, times(1)).findById(1L);
                verify(userMapper, times(1)).updateEntity(any(User.class), any(UserDTO.class));
                verify(userRepository, times(1)).save(any(User.class));
                verify(userMapper, times(1)).toDTO(any(User.class));
        }

        @Test
        void updateUser_WithInvalidId_ShouldThrowResourceNotFoundException() {
                // Given
                when(userRepository.findById(999L)).thenReturn(Optional.empty());

                // When & Then
                ResourceNotFoundException exception = assertThrows(
                                ResourceNotFoundException.class,
                                () -> userService.updateUser(999L, testUserDTO));

                assertEquals("User not found with id: 999", exception.getMessage());
                verify(userRepository, times(1)).findById(999L);
                verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void deleteUser_WithValidId_ShouldDeleteUser() {
                // Given
                when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
                doNothing().when(userRepository).delete(testUser);

                // When
                assertDoesNotThrow(() -> userService.deleteUser(1L));

                // Then
                verify(userRepository, times(1)).findById(1L);
                verify(userRepository, times(1)).delete(testUser);
        }

        @Test
        void deleteUser_WithInvalidId_ShouldThrowResourceNotFoundException() {
                // Given
                when(userRepository.findById(999L)).thenReturn(Optional.empty());

                // When & Then
                ResourceNotFoundException exception = assertThrows(
                                ResourceNotFoundException.class,
                                () -> userService.deleteUser(999L));

                assertEquals("User not found with id: 999", exception.getMessage());
                verify(userRepository, times(1)).findById(999L);
                verify(userRepository, never()).delete(any(User.class));
        }

        @Test
        void createUser_ShouldSetPasswordFromDTO() {
                // Given
                UserDTO userDTOWithPassword = UserDTO.builder()
                                .name("Test User")
                                .email("test@example.com")
                                .password("testPassword123")
                                .role(UserRole.EMPLOYEE)
                                .build();

                User userEntity = User.builder()
                                .name("Test User")
                                .email("test@example.com")
                                .password("testPassword123")
                                .role(UserRole.EMPLOYEE)
                                .build();

                User savedUser = User.builder()
                                .id(1L)
                                .name("Test User")
                                .email("test@example.com")
                                .password("testPassword123")
                                .role(UserRole.EMPLOYEE)
                                .build();

                when(userMapper.toEntity(userDTOWithPassword)).thenReturn(userEntity);
                when(userRepository.save(any(User.class))).thenReturn(savedUser);
                when(userMapper.toDTO(any(User.class))).thenReturn(userDTOWithPassword);

                // When
                UserDTO result = userService.createUser(userDTOWithPassword);

                // Then
                assertNotNull(result);
                verify(userMapper).toEntity(userDTOWithPassword);
                verify(userRepository).save(argThat(user -> "testPassword123".equals(user.getPassword()) &&
                                "Test User".equals(user.getName()) &&
                                "test@example.com".equals(user.getEmail()) &&
                                UserRole.EMPLOYEE.equals(user.getRole())));
        }

        @Test
        void updateUser_ShouldUpdateAllFields() {
                // Given
                UserDTO updateDTO = UserDTO.builder()
                                .name("Updated Name")
                                .email("updated@example.com")
                                .password("updatedPassword123")
                                .role(UserRole.ADMIN)
                                .build();

                User existingUser = User.builder()
                                .id(1L)
                                .name("Original Name")
                                .email("original@example.com")
                                .password("originalPassword123")
                                .role(UserRole.EMPLOYEE)
                                .build();

                User updatedUser = User.builder()
                                .id(1L)
                                .name("Updated Name")
                                .email("updated@example.com")
                                .password("updatedPassword123")
                                .role(UserRole.ADMIN)
                                .build();

                UserDTO expectedDTO = UserDTO.builder()
                                .id(1L)
                                .name("Updated Name")
                                .email("updated@example.com")
                                .password("********")
                                .role(UserRole.ADMIN)
                                .build();

                when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
                when(userMapper.updateEntity(eq(existingUser), eq(updateDTO))).thenReturn(updatedUser);
                when(userRepository.save(eq(updatedUser))).thenReturn(updatedUser);
                when(userMapper.toDTO(eq(updatedUser))).thenReturn(expectedDTO);

                // When
                UserDTO result = userService.updateUser(1L, updateDTO);

                // Then
                assertNotNull(result);
                assertEquals(1L, result.getId());
                assertEquals("Updated Name", result.getName());
                assertEquals("updated@example.com", result.getEmail());
                assertEquals("********", result.getPassword());
                assertEquals(UserRole.ADMIN, result.getRole());

                verify(userRepository).findById(1L);
                verify(userMapper).updateEntity(eq(existingUser), eq(updateDTO));
                verify(userRepository).save(eq(updatedUser));
                verify(userMapper).toDTO(eq(updatedUser));
        }
}
