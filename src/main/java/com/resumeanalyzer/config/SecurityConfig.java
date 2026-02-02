package com.resumeanalyzer.config;

import com.resumeanalyzer.security.CustomUserDetailsService;
import com.resumeanalyzer.security.JwtAuthenticationFilter;
import com.resumeanalyzer.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for JWT-based authentication.
 * Configures authentication, authorization, and JWT token handling.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Configure password encoder (BCrypt for password hashing)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure authentication provider (DAO-based with user details service)
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Get authentication manager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configure JWT authentication filter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
    }

    /**
     * Configure HTTP security with JWT authentication
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for JWT (stateless API)
                .csrf(csrf -> csrf.disable())

                // Set session management to stateless (JWT doesn't use sessions)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure endpoint security
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints - no authentication required
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/health", "/api/v1/health").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // Swagger/OpenAPI documentation
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs.yaml").permitAll()

                        // Public analysis endpoints (backward compatibility)
                        .requestMatchers(HttpMethod.POST, "/api/v1/analyze").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/analyze").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/skills").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/skills").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/batch-analysis").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/analyze/batch").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/compare").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/compare").permitAll()

                        // Protected endpoints - require authentication
                        .requestMatchers("/api/resumes/**").authenticated()
                        .requestMatchers("/api/analysis/**").authenticated()
                        .requestMatchers("/api/users/**").authenticated()

                        // Admin endpoints - require ROLE_ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                // Set authentication provider
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
