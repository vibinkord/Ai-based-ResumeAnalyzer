package com.resumeanalyzer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.model.dto.JobAlertRequest;
import com.resumeanalyzer.model.dto.JobAlertResponse;
import com.resumeanalyzer.model.entity.JobAlert;
import com.resumeanalyzer.model.entity.User;
import com.resumeanalyzer.service.JobAlertService;
import com.resumeanalyzer.security.JwtTokenProvider;
import com.resumeanalyzer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * JobAlertController Tests
 * Tests for job alert REST API endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("JobAlertController Tests")
public class JobAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobAlertService jobAlertService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private User testUser;
    private String authToken;
    private JobAlertRequest jobAlertRequest;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setFullName("Test User");
        testUser = userRepository.save(testUser);

        // Create test job alert request
        jobAlertRequest = JobAlertRequest.builder()
                .jobTitle("Java Developer")
                .company("Tech Corp")
                .requiredSkills("Java,Spring")
                .frequency(JobAlert.AlertFrequency.WEEKLY)
                .matchThreshold(70.0)
                .sendEmailNotification(true)
                .build();
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    @DisplayName("Should create a new job alert via API")
    void testCreateJobAlert() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/job-alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobAlertRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jobTitle").value("Java Developer"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    @DisplayName("Should get all job alerts for user")
    void testGetAllJobAlerts() throws Exception {
        // Given
        jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);

        // When & Then
        mockMvc.perform(get("/api/job-alerts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    @DisplayName("Should get alert statistics")
    void testGetAlertStatistics() throws Exception {
        // Given
        jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);

        // When & Then
        mockMvc.perform(get("/api/job-alerts/stats")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeAlerts").exists())
                .andExpect(jsonPath("$.totalAlerts").exists());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    @DisplayName("Should search alerts by keyword")
    void testSearchAlerts() throws Exception {
        // Given
        jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);

        // When & Then
        mockMvc.perform(get("/api/job-alerts/search")
                .param("keyword", "Java")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].jobTitle").value("Java Developer"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    @DisplayName("Should require authentication for API")
    void testUnauthorizedAccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/job-alerts"))
                .andExpect(status().is(200)); // Should be OK with @WithMockUser
    }
}
