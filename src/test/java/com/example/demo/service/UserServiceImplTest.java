package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers_ReturnsUserDTOList() {
        // Arrange
        User user1 = new User(1L, "Nguyễn Văn A", "a@gmail.com", "password1", "USER");
        User user2 = new User(2L, "Trần Thị B", "b@gmail.com", "password2", "ADMIN");
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act
        List<UserDTO> userDTOList = userService.getAllUsers();

        // Assert
        assertEquals(2, userDTOList.size());
        assertEquals("Nguyễn Văn A", userDTOList.get(0).getName());
        assertEquals("ADMIN", userDTOList.get(1).getRole());
        verify(userRepository, times(1)).findAll();
    }
}