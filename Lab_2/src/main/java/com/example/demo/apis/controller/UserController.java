package com.example.demo.apis.controller;

import com.example.demo.core.dtos.UserDTO;
import com.example.demo.services.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users with pagination", description = "Returns a paginated list of users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of users returned successfully")
    })
    @GetMapping
    public Page<UserDTO> getAllUsers(@org.springframework.data.web.PageableDefault(size = 10) Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @Operation(summary = "Get user by ID", description = "Returns a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found and returned"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDto) {
        UserDTO savedUser = userService.createUser(userDto);
        return ResponseEntity.ok(savedUser);
    }

    @Operation(summary = "Update an existing user", description = "Updates the user with the given ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDto) {
        UserDTO updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete a user", description = "Deletes the user with the given ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
