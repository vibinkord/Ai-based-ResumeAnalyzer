package com.resumeanalyzer.service;

import com.resumeanalyzer.model.dto.JobAlertRequest;
import com.resumeanalyzer.model.dto.JobAlertResponse;
import com.resumeanalyzer.model.entity.JobAlert;
import com.resumeanalyzer.model.entity.User;
import com.resumeanalyzer.repository.JobAlertRepository;
import com.resumeanalyzer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JobAlertService Tests
 * Tests for job alert CRUD operations and business logic
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("JobAlertService Tests")
public class JobAlertServiceTest {

    @Autowired
    private JobAlertService jobAlertService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobAlertRepository jobAlertRepository;

    private User testUser;
    private JobAlertRequest jobAlertRequest;

    @BeforeEach
    void setUp() {
        // Clean up database
        jobAlertRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setFullName("Test User");
        testUser = userRepository.save(testUser);

        // Create test job alert request
        jobAlertRequest = JobAlertRequest.builder()
                .jobTitle("Java Developer")
                .company("Tech Corp")
                .requiredSkills("Java,Spring,Hibernate")
                .frequency(JobAlert.AlertFrequency.WEEKLY)
                .matchThreshold(70.0)
                .sendEmailNotification(true)
                .build();
    }

    @Test
    @DisplayName("Should create a new job alert successfully")
    void testCreateJobAlert() {
        // When
        JobAlertResponse response = jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);

        // Then
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Java Developer", response.getJobTitle());
        assertEquals("Tech Corp", response.getCompany());
        assertTrue(response.getIsActive());
    }

    @Test
    @DisplayName("Should throw exception when creating alert for non-existent user")
    void testCreateJobAlert_UserNotFound() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> jobAlertService.createJobAlert(999L, jobAlertRequest));
    }

    @Test
    @DisplayName("Should retrieve a job alert by ID")
    void testGetJobAlertById() {
        // Given
        JobAlertResponse created = jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);

        // When
        Optional<JobAlertResponse> found = jobAlertService.getJobAlertById(created.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
        assertEquals("Java Developer", found.get().getJobTitle());
    }

    @Test
    @DisplayName("Should retrieve all job alerts for a user")
    void testGetAlertsByUser() {
        // Given
        jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);
        
        JobAlertRequest request2 = JobAlertRequest.builder()
                .jobTitle("Python Developer")
                .company("Code Inc")
                .frequency(JobAlert.AlertFrequency.DAILY)
                .matchThreshold(60.0)
                .sendEmailNotification(true)
                .build();
        jobAlertService.createJobAlert(testUser.getId(), request2);

        // When
        List<JobAlertResponse> alerts = jobAlertService.getAlertsByUser(testUser.getId());

        // Then
        assertEquals(2, alerts.size());
    }

    @Test
    @DisplayName("Should update a job alert successfully")
    void testUpdateJobAlert() {
        // Given
        JobAlertResponse created = jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);
        JobAlertRequest updateRequest = JobAlertRequest.builder()
                .jobTitle("Senior Java Developer")
                .company("Tech Corp")
                .frequency(JobAlert.AlertFrequency.DAILY)
                .matchThreshold(75.0)
                .sendEmailNotification(true)
                .build();

        // When
        JobAlertResponse updated = jobAlertService.updateJobAlert(created.getId(), testUser.getId(), updateRequest);

        // Then
        assertEquals("Senior Java Developer", updated.getJobTitle());
        assertEquals(75.0, updated.getMatchThreshold());
    }

    @Test
    @DisplayName("Should delete a job alert successfully")
    void testDeleteJobAlert() {
        // Given
        JobAlertResponse created = jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);

        // When
        jobAlertService.deleteJobAlert(created.getId(), testUser.getId());

        // Then
        Optional<JobAlertResponse> found = jobAlertService.getJobAlertById(created.getId());
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should deactivate a job alert (soft delete)")
    void testDeactivateJobAlert() {
        // Given
        JobAlertResponse created = jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);

        // When
        jobAlertService.deactivateJobAlert(created.getId(), testUser.getId());

        // Then
        Optional<JobAlertResponse> found = jobAlertService.getJobAlertById(created.getId());
        assertTrue(found.isPresent());
        assertFalse(found.get().getIsActive());
    }

    @Test
    @DisplayName("Should reactivate a deactivated job alert")
    void testReactivateJobAlert() {
        // Given
        JobAlertResponse created = jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);
        jobAlertService.deactivateJobAlert(created.getId(), testUser.getId());

        // When
        jobAlertService.reactivateJobAlert(created.getId(), testUser.getId());

        // Then
        Optional<JobAlertResponse> found = jobAlertService.getJobAlertById(created.getId());
        assertTrue(found.isPresent());
        assertTrue(found.get().getIsActive());
    }

    @Test
    @DisplayName("Should search alerts by job title")
    void testSearchAlertsByJobTitle() {
        // Given
        jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);
        
        JobAlertRequest request2 = JobAlertRequest.builder()
                .jobTitle("Project Manager")
                .company("Code Inc")
                .frequency(JobAlert.AlertFrequency.DAILY)
                .matchThreshold(60.0)
                .sendEmailNotification(true)
                .build();
        jobAlertService.createJobAlert(testUser.getId(), request2);

        // When
        List<JobAlertResponse> results = jobAlertService.searchAlertsByJobTitle(testUser.getId(), "Java");

        // Then
        assertEquals(1, results.size());
        assertEquals("Java Developer", results.get(0).getJobTitle());
    }

    @Test
    @DisplayName("Should count active alerts for a user")
    void testGetActiveAlertCount() {
        // Given
        jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);
        jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);

        // When
        long count = jobAlertService.getActiveAlertCount(testUser.getId());

        // Then
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should check if user has active alerts")
    void testHasActiveAlerts() {
        // Given
        jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);

        // When
        boolean hasAlerts = jobAlertService.hasActiveAlerts(testUser.getId());

        // Then
        assertTrue(hasAlerts);
    }

    @Test
    @DisplayName("Should throw exception when updating alert with wrong user")
    void testUpdateJobAlert_UnauthorizedUser() {
        // Given
        final User otherUser = new User();
        otherUser.setEmail("other@example.com");
        otherUser.setPassword("hashedPassword");
        otherUser.setFullName("Other User");
        final User savedOtherUser = userRepository.save(otherUser);

        JobAlertResponse created = jobAlertService.createJobAlert(testUser.getId(), jobAlertRequest);

        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> jobAlertService.updateJobAlert(created.getId(), savedOtherUser.getId(), jobAlertRequest));
    }
}
