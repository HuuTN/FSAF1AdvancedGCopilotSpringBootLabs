package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // implement service layer
    @Autowired
    private UserService userService;

    // Endpoint to get a user by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Endpoint to get all users
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // Endpoint to add a new user
    @PostMapping
    public void addUser(@Valid @RequestBody UserDTO userDTO) {
        userService.addUser(userDTO.fromEntity());
    }

    // Endpoint to update a user
    @PutMapping("/{id}")
    public void updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        userService.updateUser(userDTO.fromEntity());
    }

    // Endpoint to delete a user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // Export collection as JSON file
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportUsersAsJson() throws Exception {
        
        List<UserDTO> users = userService.getAllUsers();
        ObjectMapper mapper = new ObjectMapper();
        byte[] jsonData = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(users);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(jsonData);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.json");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(jsonData.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(byteArrayInputStream));
    }
}
