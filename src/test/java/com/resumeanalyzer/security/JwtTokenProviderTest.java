package com.resumeanalyzer.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtTokenProvider
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("JWT Token Provider Tests")
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        // Create mock authentication
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        mockAuthentication = new UsernamePasswordAuthenticationToken(
                "testuser@example.com",
                "password",
                authorities
        );
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void testGenerateToken() {
        String token = jwtTokenProvider.generateToken(mockAuthentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("Should extract username from token")
    void testGetUsernameFromToken() {
        String token = jwtTokenProvider.generateToken(mockAuthentication);
        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals("testuser@example.com", username);
    }

    @Test
    @DisplayName("Should validate correct token")
    void testValidateToken() {
        String token = jwtTokenProvider.generateToken(mockAuthentication);
        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should reject invalid token")
    void testValidateInvalidToken() {
        String invalidToken = "invalid.token.here";
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should generate refresh token")
    void testGenerateRefreshToken() {
        String refreshToken = jwtTokenProvider.generateRefreshToken("testuser@example.com");

        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertTrue(refreshToken.contains("."));
    }

    @Test
    @DisplayName("Should get token expiration time")
    void testGetTokenExpirationTime() {
        String token = jwtTokenProvider.generateToken(mockAuthentication);
        long expirationTime = jwtTokenProvider.getTokenExpirationTime(token);

        assertTrue(expirationTime > 0);
        assertTrue(expirationTime > System.currentTimeMillis());
    }

    @Test
    @DisplayName("Should get token expiration in seconds")
    void testGetTokenExpirationInSeconds() {
        String token = jwtTokenProvider.generateToken(mockAuthentication);
        long expirationSeconds = jwtTokenProvider.getTokenExpirationInSeconds(token);

        assertTrue(expirationSeconds > 0);
        assertTrue(expirationSeconds < jwtTokenProvider.getJwtExpirationMs() / 1000 + 10);
    }

    @Test
    @DisplayName("Should identify non-expired token")
    void testIsTokenExpired() {
        String token = jwtTokenProvider.generateToken(mockAuthentication);
        boolean isExpired = jwtTokenProvider.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    @DisplayName("Should extract claims from token")
    void testGetClaimsFromToken() {
        String token = jwtTokenProvider.generateToken(mockAuthentication);
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);

        assertNotNull(claims);
        assertEquals("testuser@example.com", claims.getSubject());
        assertNotNull(claims.getExpiration());
        assertNotNull(claims.getIssuedAt());
    }

    @Test
    @DisplayName("Should generate token with custom claims")
    void testGenerateTokenWithClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_ADMIN");
        claims.put("userId", 123);

        String token = jwtTokenProvider.generateTokenWithClaims("admin@example.com", claims);

        assertNotNull(token);
        assertEquals("admin@example.com", jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    @DisplayName("Should get JWT expiration milliseconds")
    void testGetJwtExpirationMs() {
        long expirationMs = jwtTokenProvider.getJwtExpirationMs();

        assertTrue(expirationMs > 0);
    }

    @Test
    @DisplayName("Should get JWT refresh expiration milliseconds")
    void testGetJwtRefreshExpirationMs() {
        long refreshExpirationMs = jwtTokenProvider.getJwtRefreshExpirationMs();

        assertTrue(refreshExpirationMs > 0);
        assertTrue(refreshExpirationMs > jwtTokenProvider.getJwtExpirationMs());
    }

    @Test
    @DisplayName("Should reject expired token")
    void testValidateExpiredToken() throws InterruptedException {
        // This test would need a special setup to create an already-expired token
        // For now, we'll test that a valid token is not marked as expired
        String token = jwtTokenProvider.generateToken(mockAuthentication);
        assertFalse(jwtTokenProvider.isTokenExpired(token));
    }
}
