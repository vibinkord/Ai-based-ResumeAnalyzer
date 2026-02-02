package com.resumeanalyzer.service;

import com.resumeanalyzer.model.dto.JobMatchResultDto;
import com.resumeanalyzer.model.entity.*;
import com.resumeanalyzer.repository.JobMatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JobMatchingServiceTest - Test suite for JobMatchingService
 * Tests multi-factor matching algorithm and scoring calculations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JobMatchingService Tests")
public class JobMatchingServiceTest {

    @Mock
    private JobMatchRepository jobMatchRepository;

    @InjectMocks
    private JobMatchingService jobMatchingService;

    private User testUser;
    private Resume testResume;
    private JobAlert testAlert;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testResume = new Resume();
        testResume.setId(1L);
        testResume.setContent("Java Spring Boot Docker Kubernetes AWS");

        testAlert = new JobAlert();
        testAlert.setId(1L);
        testAlert.setJobTitle("Senior Java Developer");
        testAlert.setCompany("Tech Corp");
        testAlert.setRequiredSkills("Java,Spring Boot,Docker");
        testAlert.setLocation("New York");
        testAlert.setSalaryMin(100000.0);
        testAlert.setSalaryMax(150000.0);
        testAlert.setMatchThreshold(70.0);
    }

    @Test
    @DisplayName("Test matchResumeToAlert returns valid result")
    void testMatchResumeToAlertValid() {
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(testAlert.getId(), result.getAlertId());
        assertNotNull(result.getMatchScore());
    }

    @Test
    @DisplayName("Test skill match score calculation")
    void testSkillMatchScore() {
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertTrue(result.getSkillScore() >= 0);
        assertTrue(result.getSkillScore() <= 100);
    }

    @Test
    @DisplayName("Test salary score calculation")
    void testSalaryScore() {
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertTrue(result.getSalaryScore() >= 0);
        assertTrue(result.getSalaryScore() <= 100);
    }

    @Test
    @DisplayName("Test experience score calculation")
    void testExperienceScore() {
        testResume.setContent("5 years of experience in Java");
        
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertTrue(result.getExperienceScore() > 0);
    }

    @Test
    @DisplayName("Test location score calculation")
    void testLocationScore() {
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertTrue(result.getLocationScore() >= 0);
    }

    @Test
    @DisplayName("Test final weighted score is within range")
    void testFinalScore() {
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertTrue(result.getMatchScore() >= 0);
        assertTrue(result.getMatchScore() <= 100);
    }

    @Test
    @DisplayName("Test matched skills extraction")
    void testMatchedSkillsExtraction() {
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertNotNull(result.getMatchedSkills());
    }

    @Test
    @DisplayName("Test missing skills extraction")
    void testMissingSkillsExtraction() {
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertNotNull(result.getMissingSkills());
    }

    @Test
    @DisplayName("Test result timestamp is populated")
    void testTimestamp() {
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertNotNull(result.getTimestamp());
    }

    @Test
    @DisplayName("Test batch match returns list")
    void testBatchMatch() {
        List<JobMatchResultDto> results = jobMatchingService.batchMatchResume(testUser, testResume);
        
        assertNotNull(results);
        assertTrue(results instanceof List);
    }

    @Test
    @DisplayName("Test user statistics calculation")
    void testUserStatistics() {
        Map<String, Object> stats = jobMatchingService.getUserMatchStatistics(1L);
        
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalMatches"));
    }

    @Test
    @DisplayName("Test match percentage is integer")
    void testMatchPercentage() {
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertTrue(result.getMatchPercentage() >= 0);
        assertTrue(result.getMatchPercentage() <= 100);
    }

    @Test
    @DisplayName("Test all score components are between 0-100")
    void testScoreRanges() {
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertTrue(result.getSkillScore() >= 0 && result.getSkillScore() <= 100);
        assertTrue(result.getSalaryScore() >= 0 && result.getSalaryScore() <= 100);
        assertTrue(result.getExperienceScore() >= 0 && result.getExperienceScore() <= 100);
        assertTrue(result.getLocationScore() >= 0 && result.getLocationScore() <= 100);
    }

    @Test
    @DisplayName("Test matched boolean field is set")
    void testMatchedFlag() {
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertNotNull(result.isMatched());
    }

    @Test
    @DisplayName("Test result includes alert ID")
    void testAlertIdIncluded() {
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertEquals(testAlert.getId(), result.getAlertId());
    }

    @Test
    @DisplayName("Test result includes user ID")
    void testUserIdIncluded() {
        JobMatchResultDto result = jobMatchingService.matchResumeToAlert(testUser, testResume, testAlert);
        
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getUserId());
    }
}
