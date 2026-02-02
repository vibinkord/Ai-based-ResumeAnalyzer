package com.resumeanalyzer.web.controller;

import com.resumeanalyzer.web.dto.BatchAnalysisRequest;
import com.resumeanalyzer.web.dto.ComparisonRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UtilityController class.
 * Tests health check, skills list, batch analysis, and comparison endpoints.
 */
@WebMvcTest(UtilityController.class)
@DisplayName("UtilityController Tests")
class UtilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ===================== Health Check Endpoint Tests =====================

    @Test
    @DisplayName("Should return health check status")
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/v1/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Health check should return UP status")
    void testHealthCheckStatus() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Health check should include version")
    void testHealthCheckVersion() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").isNotEmpty());
    }

    // ===================== Skills List Endpoint Tests =====================

    @Test
    @DisplayName("Should return list of all known skills")
    void testGetSkills() throws Exception {
        mockMvc.perform(get("/api/v1/skills")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    @DisplayName("Skills list should contain at least 100 skills")
    void testSkillsCount() throws Exception {
        mockMvc.perform(get("/api/v1/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount", greaterThanOrEqualTo(100)));
    }

    @Test
    @DisplayName("Skills list should contain Java")
    void testSkillsContainsJava() throws Exception {
        mockMvc.perform(get("/api/v1/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills[*]", hasItem("Java")));
    }

    @Test
    @DisplayName("Skills list should contain Python")
    void testSkillsContainsPython() throws Exception {
        mockMvc.perform(get("/api/v1/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills[*]", hasItem("Python")));
    }

    @Test
    @DisplayName("Skills list should contain Spring")
    void testSkillsContainsSpring() throws Exception {
        mockMvc.perform(get("/api/v1/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills[*]", hasItem("Spring")));
    }

    // ===================== Batch Analysis Endpoint Tests =====================

    @Test
    @DisplayName("Should process batch analysis request")
    void testBatchAnalysis() throws Exception {
        BatchAnalysisRequest request = new BatchAnalysisRequest();
        List<BatchAnalysisRequest.Item> items = new ArrayList<>();
        items.add(new BatchAnalysisRequest.Item("1", "Java Developer", "Java, Spring"));
        request.setItems(items);

        mockMvc.perform(post("/api/v1/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.successCount").isNumber())
                .andExpect(jsonPath("$.failureCount").isNumber())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Batch analysis should reject empty request")
    void testBatchAnalysisEmptyRequest() throws Exception {
        BatchAnalysisRequest request = new BatchAnalysisRequest();
        request.setItems(new ArrayList<>());

        mockMvc.perform(post("/api/v1/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Batch analysis should handle null items")
    void testBatchAnalysisNullItems() throws Exception {
        BatchAnalysisRequest request = new BatchAnalysisRequest();
        request.setItems(null);

        mockMvc.perform(post("/api/v1/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Batch analysis should return success count")
    void testBatchAnalysisSuccessCount() throws Exception {
        BatchAnalysisRequest request = new BatchAnalysisRequest();
        List<BatchAnalysisRequest.Item> items = new ArrayList<>();
        items.add(new BatchAnalysisRequest.Item("1", "Java Developer", "Java, Spring"));
        items.add(new BatchAnalysisRequest.Item("2", "Python Developer", "Python, Django"));
        request.setItems(items);

        mockMvc.perform(post("/api/v1/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount", greaterThanOrEqualTo(0)));
    }

    // ===================== Resume Comparison Endpoint Tests =====================

    @Test
    @DisplayName("Should compare two resumes")
    void testCompareResumes() throws Exception {
        ComparisonRequest request = new ComparisonRequest(
                "Java, Python, Spring",
                "Java, Spring, React"
        );

        mockMvc.perform(post("/api/v1/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resume1SkillCount").isNumber())
                .andExpect(jsonPath("$.resume2SkillCount").isNumber())
                .andExpect(jsonPath("$.commonSkills").isArray())
                .andExpect(jsonPath("$.uniqueToResume1").isArray())
                .andExpect(jsonPath("$.uniqueToResume2").isArray())
                .andExpect(jsonPath("$.similarityPercentage").isNumber())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Comparison should reject missing resume1")
    void testComparisonMissingResume1() throws Exception {
        ComparisonRequest request = new ComparisonRequest(null, "Java, Python");

        mockMvc.perform(post("/api/v1/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Comparison should reject missing resume2")
    void testComparisonMissingResume2() throws Exception {
        ComparisonRequest request = new ComparisonRequest("Java, Python", null);

        mockMvc.perform(post("/api/v1/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Comparison should reject empty resume1")
    void testComparisonEmptyResume1() throws Exception {
        ComparisonRequest request = new ComparisonRequest("", "Java, Python");

        mockMvc.perform(post("/api/v1/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Comparison should detect common skills")
    void testComparisonCommonSkills() throws Exception {
        ComparisonRequest request = new ComparisonRequest(
                "Java, Python",
                "Java, Python"
        );

        mockMvc.perform(post("/api/v1/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commonSkills").isArray())
                .andExpect(jsonPath("$.similarityPercentage", greaterThan(0.0)));
    }

    @Test
    @DisplayName("Comparison should calculate similarity percentage")
    void testComparisonSimilarityPercentage() throws Exception {
        ComparisonRequest request = new ComparisonRequest(
                "Java",
                "Java"
        );

        mockMvc.perform(post("/api/v1/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.similarityPercentage").isNumber());
    }

    // ===================== Endpoint Availability Tests =====================

    @Test
    @DisplayName("Health endpoint should be accessible")
    void testHealthEndpointAccessible() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Skills endpoint should be accessible")
    void testSkillsEndpointAccessible() throws Exception {
        mockMvc.perform(get("/api/v1/skills"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Batch endpoint should be accessible")
    void testBatchEndpointAccessible() throws Exception {
        BatchAnalysisRequest request = new BatchAnalysisRequest();
        List<BatchAnalysisRequest.Item> items = new ArrayList<>();
        items.add(new BatchAnalysisRequest.Item("1", "resume", "job"));
        request.setItems(items);

        mockMvc.perform(post("/api/v1/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Compare endpoint should be accessible")
    void testCompareEndpointAccessible() throws Exception {
        ComparisonRequest request = new ComparisonRequest("resume1", "resume2");

        mockMvc.perform(post("/api/v1/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
