package com.alex.qnr_project.service;

import com.alex.qnr_project.entity.BlacklistedToken;
import com.alex.qnr_project.repository.BlacklistedTokenRepository;
import com.alex.qnr_project.repository.UserRepository;
import com.alex.qnr_project.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public String login(String username, String rawPassword) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getUsername());
    }

    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("No JWT token found");
        }

        String token = authHeader.substring(7);

        BlacklistedToken blacklisted = BlacklistedToken.builder()
                .token(token)
                .createdAt(LocalDateTime.now())
                .build();

        blacklistedTokenRepository.save(blacklisted);
    }
}