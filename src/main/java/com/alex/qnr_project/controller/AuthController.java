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

    // login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {

        // fetch user by username
        var userOpt = userRepository.findByUsername(request.getUsername());

        //check if user exists and password matches
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        //generate token
        String token = jwtUtil.generateToken(userOpt.get().getUsername());

        //return token in response
        return ResponseEntity.ok(new AuthResponse(token));
    }

    // logout endpoint
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization")  String authHeader) {

        //checks if authorization header exists and is valid
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("No JWT token found");
        }

        //extract token
        String token = authHeader.substring(7);

        // create and save blaclisted token entity to mark token as invalid
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
