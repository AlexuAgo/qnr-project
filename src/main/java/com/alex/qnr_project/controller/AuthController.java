package com.alex.qnr_project.controller;


import com.alex.qnr_project.entity.BlacklistedToken;
import com.alex.qnr_project.repository.BlacklistedTokenRepository;
import com.alex.qnr_project.repository.UserRepository;
import com.alex.qnr_project.security.JwtUtil;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;


    public AuthController(UserRepository userRepository, BlacklistedTokenRepository blacklistedTokenRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        var userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(userOpt.get().getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    // logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization")  String authHeader) {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("No JWT token found");
        }
        String token = authHeader.substring(7);

        BlacklistedToken blacklisted = BlacklistedToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .build();

        blacklistedTokenRepository.save(blacklisted);

        return ResponseEntity.ok("Logged out successfully");
    }


    // DTOs
    @Data
    static class AuthRequest {
        private String username;
        private String password;
    }

    @Data
    static class AuthResponse {
        private final String token;
    }
}
