package com.resumeanalyzer.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for authentication responses (login/register)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Authentication response with JWT token")
public class LoginResponse {

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String accessToken;

    @Schema(description = "Refresh token for getting new access token", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String refreshToken;

    @Schema(description = "Token type (Bearer)", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "User email address", example = "user@example.com")
    private String email;

    @Schema(description = "User's full name", example = "John Doe")
    private String fullName;

    @Schema(description = "User roles", example = "[\"ROLE_USER\"]")
    private Set<String> roles;

    @Schema(description = "Access token expiration time in milliseconds", example = "3600000")
    private Long expiresIn;

    @Schema(description = "When the account was created")
    private LocalDateTime createdAt;

    @Schema(description = "Message", example = "Login successful")
    private String message;

    @Schema(description = "Whether the operation was successful", example = "true")
    private Boolean success = true;
}
