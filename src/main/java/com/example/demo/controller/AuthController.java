package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.security.JwtTokenUtil;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthController(AuthenticationManager authenticationManager,
                         UserService userService,
                         PasswordEncoder passwordEncoder,
                         JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        // Kiểm tra email đã tồn tại chưa
        if (userService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email đã được sử dụng!");
        }

        // Tạo user mới
        User user = userDTO.fromEntity();
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userService.addUser(user);

        return ResponseEntity.ok("Đăng ký thành công!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        
        String jwt = jwtTokenUtil.generateToken(authentication);

        return ResponseEntity.ok(new AuthResponse(jwt, user.getEmail(), user.getRole()));
    }
}
