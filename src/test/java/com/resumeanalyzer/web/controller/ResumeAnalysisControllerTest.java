package com.resumeanalyzer.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.web.dto.ResumeAnalysisRequest;
import com.resumeanalyzer.web.dto.ResumeAnalysisResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ResumeAnalysisController.
 * Tests the REST API endpoints with Spring Boot Test.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ResumeAnalysisController Integration Tests")
class ResumeAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Setup before each test
    }

    @Test
    @DisplayName("Should successfully analyze resume against job description")
    void testAnalyzeEndpoint() throws Exception {
        ResumeAnalysisRequest request = new ResumeAnalysisRequest(
                "I am a Java developer with SQL and REST API experience",
                "We need a Java engineer with SQL and REST knowledge"
        );

        MvcResult result = mockMvc.perform(post("/api/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ResumeAnalysisResponse response = objectMapper.readValue(responseBody, ResumeAnalysisResponse.class);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getMatchPercentage(), "Match percentage should not be null");
        assertNotNull(response.getMatchedSkills(), "Matched skills should not be null");
        assertNotNull(response.getMissingSkills(), "Missing skills should not be null");
        assertNotNull(response.getSuggestions(), "Suggestions should not be null");
    }

    @Test
    @DisplayName("Should calculate match percentage correctly")
    void testMatchPercentageCalculation() throws Exception {
        ResumeAnalysisRequest request = new ResumeAnalysisRequest(
                "Java developer",
                "Need Java engineer"
        );

        MvcResult result = mockMvc.perform(post("/api/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ResumeAnalysisResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ResumeAnalysisResponse.class
        );

        assertTrue(response.getMatchPercentage() > 0, "Match percentage should be greater than 0");
        assertTrue(response.getMatchPercentage() <= 100, "Match percentage should not exceed 100");
    }

    @Test
    @DisplayName("Should extract matched skills")
    void testMatchedSkillsExtraction() throws Exception {
        ResumeAnalysisRequest request = new ResumeAnalysisRequest(
                "Java and SQL developer",
                "Need Java and SQL engineer"
        );

        MvcResult result = mockMvc.perform(post("/api/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ResumeAnalysisResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ResumeAnalysisResponse.class
        );

        assertNotNull(response.getMatchedSkills());
        assertTrue(response.getMatchedSkills().contains("Java"), "Should extract Java");
        assertTrue(response.getMatchedSkills().contains("SQL"), "Should extract SQL");
    }

    @Test
    @DisplayName("Should identify missing skills")
    void testMissingSkillsIdentification() throws Exception {
        ResumeAnalysisRequest request = new ResumeAnalysisRequest(
                "I know Java",
                "Need Java and Spring"
        );

        MvcResult result = mockMvc.perform(post("/api/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ResumeAnalysisResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ResumeAnalysisResponse.class
        );

        assertNotNull(response.getMissingSkills());
        assertTrue(response.getMissingSkills().contains("Spring"), "Should identify Spring as missing");
    }

    @Test
    @DisplayName("Should generate suggestions")
    void testSuggestionGeneration() throws Exception {
        ResumeAnalysisRequest request = new ResumeAnalysisRequest(
                "I have basic Java skills",
                "Need expert in Java, Spring, SQL, REST, and Docker"
        );

        MvcResult result = mockMvc.perform(post("/api/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ResumeAnalysisResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ResumeAnalysisResponse.class
        );

        assertNotNull(response.getSuggestions());
        assertTrue(response.getSuggestions().size() > 0, "Should generate suggestions");
    }

    @Test
    @DisplayName("Should generate report")
    void testReportGeneration() throws Exception {
        ResumeAnalysisRequest request = new ResumeAnalysisRequest(
                "Java developer with SQL",
                "Need Java engineer with SQL"
        );

        MvcResult result = mockMvc.perform(post("/api/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ResumeAnalysisResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ResumeAnalysisResponse.class
        );

        assertNotNull(response.getReport());
        assertTrue(response.getReport().length() > 0, "Report should not be empty");
    }

    @Test
    @DisplayName("Should handle full match scenario")
    void testFullMatchScenario() throws Exception {
        String resumeText = "I have Java, SQL, Spring, REST, OOP, Git, JSON, and NIO experience";
        String jobDescription = "Need Java, SQL, Spring, REST, OOP, Git, JSON, and NIO";

        ResumeAnalysisRequest request = new ResumeAnalysisRequest(resumeText, jobDescription);

        MvcResult result = mockMvc.perform(post("/api/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ResumeAnalysisResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ResumeAnalysisResponse.class
        );

        assertTrue(response.getMatchPercentage() >= 80, "Should have high match for overlapping skills");
    }

    @Test
    @DisplayName("Should handle no match scenario")
    void testNoMatchScenario() throws Exception {
        String resumeText = "I am a COBOL programmer";
        String jobDescription = "We need a Java developer";

        ResumeAnalysisRequest request = new ResumeAnalysisRequest(resumeText, jobDescription);

        MvcResult result = mockMvc.perform(post("/api/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ResumeAnalysisResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ResumeAnalysisResponse.class
        );

        assertEquals(0.0, response.getMatchPercentage(), "Should have 0% match");
    }

    @Test
    @DisplayName("Should respond with JSON content type")
    void testResponseContentType() throws Exception {
        ResumeAnalysisRequest request = new ResumeAnalysisRequest(
                "Java developer",
                "Need Java engineer"
        );

        mockMvc.perform(post("/api/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should handle complex resume text")
    void testComplexResumeAnalysis() throws Exception {
        String resumeText = "Senior Java Software Engineer with 10+ years experience in enterprise application development. " +
                "Expert in Core Java, OOP, Collections (ArrayList, HashMap, HashSet), multithreading with ExecutorService, " +
                "and NIO file I/O. Strong backend development skills using Spring Framework and Spring Boot. " +
                "Database expertise with SQL and complex queries. REST API design and JSON processing. " +
                "Version control with Git. JUnit testing experience.";

        String jobDescription = "Looking for Java Developer with Spring Boot, SQL, REST APIs, and Git experience. " +
                "OOP and design patterns required. Basic NIO and multithreading knowledge preferred.";

        ResumeAnalysisRequest request = new ResumeAnalysisRequest(resumeText, jobDescription);

        MvcResult result = mockMvc.perform(post("/api/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ResumeAnalysisResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ResumeAnalysisResponse.class
        );

        assertTrue(response.getMatchPercentage() > 50, "Should have reasonable match for experienced developer");
        assertTrue(response.getMatchedSkills().size() > 0, "Should extract multiple matched skills");
    }

    @Test
    @DisplayName("Should return status 200 for valid request")
    void testSuccessStatusCode() throws Exception {
        ResumeAnalysisRequest request = new ResumeAnalysisRequest(
                "Java developer",
                "Need Java engineer"
        );

        mockMvc.perform(post("/api/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

}
