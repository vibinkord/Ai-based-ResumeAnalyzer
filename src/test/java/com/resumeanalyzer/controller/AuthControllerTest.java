package com.resumeanalyzer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.model.dto.LoginRequest;
import com.resumeanalyzer.model.dto.RegisterRequest;
import com.resumeanalyzer.model.entity.Role;
import com.resumeanalyzer.model.entity.User;
import com.resumeanalyzer.security.JwtTokenProvider;
import com.resumeanalyzer.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setFullName("Test User");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        testUser.setRoles(roles);

        // Create login request
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Create register request
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("New User");
    }

    @Test
    @DisplayName("Should successfully login with valid credentials")
    void testLoginSuccess() throws Exception {
        when(userService.findByEmailIgnoreCase("test@example.com"))
                .thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @DisplayName("Should reject login with invalid email format")
    void testLoginInvalidEmail() throws Exception {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject login with missing password")
    void testLoginMissingPassword() throws Exception {
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should successfully register new user")
    void testRegisterSuccess() throws Exception {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setFullName(registerRequest.getFullName());
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        newUser.setRoles(roles);

        when(userService.userExists(registerRequest.getEmail())).thenReturn(false);
        when(userService.createUser(registerRequest.getEmail(), 
                registerRequest.getPassword(), 
                registerRequest.getFullName()))
                .thenReturn(newUser);
        when(userService.updateUser(newUser)).thenReturn(newUser);
        when(userService.findByEmailIgnoreCase(registerRequest.getEmail()))
                .thenReturn(Optional.of(newUser));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    @DisplayName("Should reject registration with existing email")
    void testRegisterDuplicateEmail() throws Exception {
        when(userService.userExists(registerRequest.getEmail())).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    @DisplayName("Should reject registration with invalid email format")
    void testRegisterInvalidEmail() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPassword("password123");
        invalidRequest.setFullName("Test User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject registration with short password")
    void testRegisterShortPassword() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setEmail("test@example.com");
        invalidRequest.setPassword("short");
        invalidRequest.setFullName("Test User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should successfully logout")
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should return 401 for invalid refresh token")
    void testRefreshTokenInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/refresh-token")
                .param("refreshToken", "invalid.token.here"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
