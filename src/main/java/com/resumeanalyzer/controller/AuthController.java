package com.resumeanalyzer.controller;

import com.resumeanalyzer.model.dto.LoginRequest;
import com.resumeanalyzer.model.dto.LoginResponse;
import com.resumeanalyzer.model.dto.RegisterRequest;
import com.resumeanalyzer.model.entity.Role;
import com.resumeanalyzer.model.entity.User;
import com.resumeanalyzer.security.JwtTokenProvider;
import com.resumeanalyzer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * Authentication Controller for user login and registration.
 * Provides endpoints for user authentication using JWT tokens.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * User login endpoint
     * Authenticates user credentials and returns JWT token
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Generate JWT tokens
            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication.getName());

            // Get user information
            User user = userService.findByEmailIgnoreCase(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Build response
            LoginResponse response = LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .roles(user.getRoles().stream()
                            .map(Role::name)
                            .collect(Collectors.toSet()))
                    .expiresIn(jwtTokenProvider.getJwtExpirationMs())
                    .createdAt(user.getCreatedAt())
                    .message("Login successful")
                    .success(true)
                    .build();

            log.info("User logged in successfully: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Login failed for email: {}", loginRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponse.builder()
                            .success(false)
                            .message("Invalid email or password")
                            .build());
        }
    }

    /**
     * User registration endpoint
     * Creates a new user account with default ROLE_USER role
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register new user account")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration attempt for email: {}", registerRequest.getEmail());

        try {
            // Check if user already exists
            if (userService.userExists(registerRequest.getEmail())) {
                log.warn("Registration failed - email already exists: {}", registerRequest.getEmail());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(LoginResponse.builder()
                                .success(false)
                                .message("Email already registered")
                                .build());
            }

            // Create new user with hashed password using service
            User savedUser = userService.createUser(
                    registerRequest.getEmail(),
                    passwordEncoder.encode(registerRequest.getPassword()),
                    registerRequest.getFullName()
            );

            // Add default role
            savedUser.addRole(Role.ROLE_USER);
            savedUser = userService.updateUser(savedUser);

            // Authenticate and generate tokens
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            registerRequest.getEmail(),
                            registerRequest.getPassword()
                    )
            );

            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication.getName());

            // Build response
            LoginResponse response = LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .userId(savedUser.getId())
                    .email(savedUser.getEmail())
                    .fullName(savedUser.getFullName())
                    .roles(savedUser.getRoles().stream()
                            .map(Role::name)
                            .collect(Collectors.toSet()))
                    .expiresIn(jwtTokenProvider.getJwtExpirationMs())
                    .createdAt(savedUser.getCreatedAt())
                    .message("Registration successful")
                    .success(true)
                    .build();

            log.info("User registered successfully: {}", registerRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Registration failed for email: {}", registerRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(LoginResponse.builder()
                            .success(false)
                            .message("Registration failed: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Refresh JWT token endpoint
     * Generates a new access token using a valid refresh token
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh JWT token", description = "Get new access token using refresh token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        log.debug("Token refresh requested");

        try {
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                log.warn("Invalid refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(LoginResponse.builder()
                                .success(false)
                                .message("Invalid or expired refresh token")
                                .build());
            }

            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            User user = userService.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String newAccessToken = jwtTokenProvider.generateTokenWithClaims(
                    username,
                    java.util.Map.of("roles", user.getRoles().stream()
                            .map(Role::name)
                            .toArray())
            );

            LoginResponse response = LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .expiresIn(jwtTokenProvider.getJwtExpirationMs())
                    .message("Token refreshed successfully")
                    .success(true)
                    .build();

            log.debug("Token refreshed for user: {}", username);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponse.builder()
                            .success(false)
                            .message("Token refresh failed")
                            .build());
        }
    }

    /**
     * Logout endpoint (client-side token deletion)
     * Note: JWT tokens are stateless, so server-side logout is optional
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user (client should delete JWT token)")
    public ResponseEntity<LoginResponse> logout() {
        log.debug("Logout requested");
        return ResponseEntity.ok(LoginResponse.builder()
                .success(true)
                .message("Logged out successfully. Please delete the JWT token from client.")
                .build());
    }
}
