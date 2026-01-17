package com.alex.qnr_project.security;

import com.alex.qnr_project.repository.BlacklistedTokenRepository;
import com.alex.qnr_project.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // Password hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Load users from DB
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles(    //
                                user.getRoles().stream()
                                        .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                                        .toArray(String[]::new)
                        )
                        .build())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
    }

    // JWT filter bean, extracts jwt from auth header, validates token, checks if token is blacklisted
    @Bean
    public JwtAuthFilter jwtAuthFilter(
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService,
            BlacklistedTokenRepository blacklistedTokenRepository
    ) {
        return new JwtAuthFilter(
                jwtUtil,
                userDetailsService,
                blacklistedTokenRepository
        );
    }

    // Security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth         // permit only login and logout endpoints, all others require authentication
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(           //register jwt filter
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
