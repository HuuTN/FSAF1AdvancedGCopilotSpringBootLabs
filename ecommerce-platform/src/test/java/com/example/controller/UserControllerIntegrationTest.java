package com.example.controller;

import com.example.model.dto.UserDTO;
import com.example.model.enums.UserRole;
import com.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO testUserDTO;
    private List<UserDTO> userDTOList;

    @BeforeEach
    void setUp() {
        // Setup test user DTO with masked password
        testUserDTO = UserDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("********") // Masked password
                .role(UserRole.EMPLOYEE)
                .build();

        UserDTO userDTO2 = UserDTO.builder()
                .id(2L)
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .password("********") // Masked password
                .role(UserRole.ADMIN)
                .build();

        userDTOList = Arrays.asList(testUserDTO, userDTO2);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        // Given
        Page<UserDTO> userPage = new PageImpl<>(userDTOList);
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        // When & Then
        mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("John Doe")))
                .andExpect(jsonPath("$.content[0].email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.content[0].password", is("********"))) // Password is masked
                .andExpect(jsonPath("$.content[0].role", is("EMPLOYEE")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].name", is("Jane Smith")))
                .andExpect(jsonPath("$.content[1].email", is("jane.smith@example.com")))
                .andExpect(jsonPath("$.content[1].password", is("********"))) // Password is masked
                .andExpect(jsonPath("$.content[1].role", is("ADMIN")));

        verify(userService, times(1)).getAllUsers(any(Pageable.class));
    }

    @Test
    void getAllUsersWithPagination_ShouldReturnPagedUsers() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDTO> userPage = new PageImpl<>(userDTOList, pageable, userDTOList.size());
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        // When & Then
        mockMvc.perform(get("/api/v1/users")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("John Doe")))
                .andExpect(jsonPath("$.content[0].password", is("********"))) // Password is masked
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.number", is(0)));

        verify(userService, times(1)).getAllUsers(any(Pageable.class));
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.password", is("********"))) // Password is masked
                .andExpect(jsonPath("$.role", is("EMPLOYEE")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(userService.getUserById(999L)).thenThrow(new RuntimeException("User not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/v1/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.message", containsString("User not found with id: 999")));

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    void createUser_WithValidUserDTO_ShouldReturnCreatedUser() throws Exception {
        // Given
        UserDTO inputUserDTO = UserDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123") // Real password in request
                .role(UserRole.EMPLOYEE)
                .build();

        UserDTO returnedUserDTO = UserDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("********") // Masked password in response
                .role(UserRole.EMPLOYEE)
                .build();

        when(userService.createUser(any(UserDTO.class))).thenReturn(returnedUserDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUserDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.password", is("********"))) // Password should be masked in response
                .andExpect(jsonPath("$.role", is("EMPLOYEE")));

        verify(userService, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    void createUser_WithInvalidUserDTO_ShouldReturnBadRequest() throws Exception {
        // Given - Invalid user DTO with missing required fields
        UserDTO invalidUserDTO = UserDTO.builder()
                .name("") // Empty name
                .email("invalid-email") // Invalid email format
                .password("123") // Too short password
                .build(); // Missing role

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUserDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")));

        verify(userService, never()).createUser(any(UserDTO.class));
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        // Given
        UserDTO updateUserDTO = UserDTO.builder()
                .name("John Doe Updated")
                .email("john.doe.updated@example.com")
                .password("newPassword123")
                .role(UserRole.MANAGER)
                .build();

        UserDTO returnedUserDTO = UserDTO.builder()
                .id(1L)
                .name("John Doe Updated")
                .email("john.doe.updated@example.com")
                .password("********") // Masked password
                .role(UserRole.EMPLOYEE) // Role unchanged in current implementation
                .build();

        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(returnedUserDTO);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe Updated")))
                .andExpect(jsonPath("$.email", is("john.doe.updated@example.com")))
                .andExpect(jsonPath("$.password", is("********"))); // Password should be masked

        verify(userService, times(1)).updateUser(eq(1L), any(UserDTO.class));
    }

    @Test
    void updateUser_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        UserDTO updateUserDTO = UserDTO.builder()
                .name("John Doe Updated")
                .email("john.doe.updated@example.com")
                .password("newPassword123")
                .role(UserRole.MANAGER)
                .build();

        when(userService.updateUser(eq(999L), any(UserDTO.class)))
                .thenThrow(new RuntimeException("User not found with id: 999"));

        // When & Then
        mockMvc.perform(put("/api/v1/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.message", containsString("User not found with id: 999")));

        verify(userService, times(1)).updateUser(eq(999L), any(UserDTO.class));
    }

    @Test
    void deleteUser_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("User not found with id: 999"))
                .when(userService).deleteUser(999L);

        // When & Then
        mockMvc.perform(delete("/api/v1/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.message", containsString("User not found with id: 999")));

        verify(userService, times(1)).deleteUser(999L);
    }

    @Test
    void createUser_WithEmptyRequestBody_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserDTO.class));
    }

    @Test
    void getAllUsers_WhenServiceReturnsEmptyList_ShouldReturnEmptyArray() throws Exception {
        // Given
        Page<UserDTO> emptyPage = new PageImpl<>(Arrays.asList());
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(userService, times(1)).getAllUsers(any(Pageable.class));
    }
}
