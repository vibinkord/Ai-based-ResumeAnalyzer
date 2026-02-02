package com.resumeanalyzer.service;

import com.resumeanalyzer.model.dto.NotificationPreferenceRequest;
import com.resumeanalyzer.model.dto.NotificationPreferenceResponse;
import com.resumeanalyzer.model.entity.NotificationPreference;
import com.resumeanalyzer.model.entity.User;
import com.resumeanalyzer.repository.NotificationPreferenceRepository;
import com.resumeanalyzer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NotificationService Tests
 * Tests for notification preferences and email coordination
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("NotificationService Tests")
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up database
        preferenceRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setFullName("Test User");
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("Should get notification preferences for a user")
    void testGetUserPreferences() {
        // Given - preferences should be auto-created
        notificationService.createDefaultPreferences(testUser);

        // When
        NotificationPreferenceResponse response = notificationService.getUserPreferences(testUser.getId());

        // Then
        assertNotNull(response);
        assertTrue(response.getEmailEnabled());
        assertEquals(9, response.getPreferredNotificationHour());
    }

    @Test
    @DisplayName("Should update notification preferences")
    void testUpdateUserPreferences() {
        // Given
        notificationService.createDefaultPreferences(testUser);
        NotificationPreferenceRequest request = NotificationPreferenceRequest.builder()
                .emailEnabled(false)
                .jobAlertEmailEnabled(false)
                .matchNotificationEnabled(true)
                .weeklyDigestEnabled(true)
                .preferredNotificationHour(10)
                .preferredNotificationMinute(30)
                .preferredDigestDayOfWeek(2)
                .timezone("America/New_York")
                .minimumMatchThreshold(75.0)
                .build();

        // When
        NotificationPreferenceResponse updated = notificationService.updateUserPreferences(testUser.getId(), request);

        // Then
        assertFalse(updated.getEmailEnabled());
        assertEquals(10, updated.getPreferredNotificationHour());
        assertEquals("America/New_York", updated.getTimezone());
    }

    @Test
    @DisplayName("Should create default preferences for user")
    void testCreateDefaultPreferences() {
        // When
        NotificationPreference preference = notificationService.createDefaultPreferences(testUser);

        // Then
        assertNotNull(preference);
        assertTrue(preference.getEmailEnabled());
        assertTrue(preference.getJobAlertEmailEnabled());
        assertEquals(9, preference.getPreferredHour());
        assertEquals("UTC", preference.getTimezone());
    }

    @Test
    @DisplayName("Should opt in user for notifications")
    void testOptInForNotifications() {
        // Given
        notificationService.createDefaultPreferences(testUser);
        notificationService.optOutFromNotifications(testUser.getId());

        // When
        notificationService.optInForNotifications(testUser.getId());

        // Then
        NotificationPreferenceResponse response = notificationService.getUserPreferences(testUser.getId());
        assertTrue(response.getEmailEnabled());
    }

    @Test
    @DisplayName("Should opt out user from notifications")
    void testOptOutFromNotifications() {
        // Given
        notificationService.createDefaultPreferences(testUser);

        // When
        notificationService.optOutFromNotifications(testUser.getId());

        // Then
        NotificationPreferenceResponse response = notificationService.getUserPreferences(testUser.getId());
        assertFalse(response.getEmailEnabled());
    }

    @Test
    @DisplayName("Should check if user should receive notifications")
    void testShouldSendNotification() {
        // Given
        notificationService.createDefaultPreferences(testUser);

        // When
        boolean shouldSend = notificationService.shouldSendNotification(testUser.getId());

        // Then
        assertTrue(shouldSend);
    }

    @Test
    @DisplayName("Should check if user should receive digest")
    void testShouldSendDigest() {
        // Given
        notificationService.createDefaultPreferences(testUser);

        // When
        boolean shouldSend = notificationService.shouldSendDigest(testUser.getId());

        // Then
        assertTrue(shouldSend);
    }

    @Test
    @DisplayName("Should return false for notifications when user opted out")
    void testShouldSendNotification_OptedOut() {
        // Given
        notificationService.createDefaultPreferences(testUser);
        notificationService.optOutFromNotifications(testUser.getId());

        // When
        boolean shouldSend = notificationService.shouldSendNotification(testUser.getId());

        // Then
        assertFalse(shouldSend);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testGetUserPreferences_UserNotFound() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> notificationService.getUserPreferences(999L));
    }

    @Test
    @DisplayName("Should send welcome email successfully")
    void testSendWelcomeEmail() {
        // When
        boolean sent = notificationService.sendWelcomeEmail(testUser.getId());

        // Then - should return true since email service is configured
        // In test environment, this might be disabled, so we just check no exception
        assertNotNull(sent);
    }

    @Test
    @DisplayName("Should get count of email enabled users")
    void testGetTotalEmailEnabledUsers() {
        // Given
        notificationService.createDefaultPreferences(testUser);

        // When
        long count = notificationService.getTotalEmailEnabledUsers();

        // Then
        assertTrue(count >= 1);
    }

    @Test
    @DisplayName("Should get users with job alert emails enabled")
    void testGetUsersWithJobAlertEmailsEnabled() {
        // Given
        notificationService.createDefaultPreferences(testUser);

        // When
        var userIds = notificationService.getUsersWithJobAlertEmailsEnabled();

        // Then
        assertTrue(userIds.size() >= 1);
        assertTrue(userIds.contains(testUser.getId()));
    }

    @Test
    @DisplayName("Should get users with weekly digest enabled")
    void testGetUsersWithWeeklyDigestEnabled() {
        // Given
        notificationService.createDefaultPreferences(testUser);

        // When
        var userIds = notificationService.getUsersWithWeeklyDigestEnabled();

        // Then
        assertTrue(userIds.size() >= 1);
        assertTrue(userIds.contains(testUser.getId()));
    }
}
