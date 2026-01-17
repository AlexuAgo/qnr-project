package com.alex.qnr_project.security;

import com.alex.qnr_project.repository.BlacklistedTokenRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public JwtAuthFilter(JwtUtil jwtUtil,
                         UserDetailsService userDetailsService,
                         BlacklistedTokenRepository blacklistedTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println("JWT Filter Debug");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Authorization header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Token extracted: " + token);

            // Check blacklist
            boolean blacklisted = blacklistedTokenRepository.findByToken(token).isPresent();
            System.out.println("Token blacklisted? " + blacklisted);

            if (blacklisted) {
                SecurityContextHolder.clearContext(); // used because sometimes the token that is blacklisted can still be used to execute requests
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Token is blacklisted");
                return;
            }

            // Validate JWT
            if (jwtUtil.validateJwtToken(token)) {
                String username = jwtUtil.getUserFromToken(token);
                System.out.println("JWT valid, username: " + username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                System.out.println("Loaded user details: " + userDetails.getUsername());
                System.out.println("Authorities: " + userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()  // MUST be populated
                        );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Authentication set in SecurityContext");
            } else {
                System.out.println("JWT validation failed!");
            }
        } else {
            System.out.println("No Bearer token found in request");
        }

        filterChain.doFilter(request, response);
    }
}
