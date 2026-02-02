package com.resumeanalyzer.service;

import com.resumeanalyzer.model.entity.*;
import com.resumeanalyzer.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * AnalyticsServiceTest - Test suite for AnalyticsService
 * Tests dashboard metrics, distributions, trends, and reporting
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AnalyticsService Tests")
public class AnalyticsServiceTest {

    @Mock
    private JobAlertRepository jobAlertRepository;

    @Mock
    private JobMatchRepository jobMatchRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private AnalysisRepository analysisRepository;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        when(jobAlertRepository.countByUserIdAndIsActiveTrue(1L)).thenReturn(3L);
        when(jobAlertRepository.countByUserId(1L)).thenReturn(5L);
        when(jobMatchRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        when(resumeRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        when(analysisRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
    }

    @Test
    @DisplayName("Test getUserDashboard returns map")
    void testGetUserDashboard() {
        Map<String, Object> dashboard = analyticsService.getUserDashboard(1L);
        
        assertNotNull(dashboard);
        assertFalse(dashboard.isEmpty());
    }

    @Test
    @DisplayName("Test getUserDashboard contains activeAlerts")
    void testUserDashboardHasActiveAlerts() {
        Map<String, Object> dashboard = analyticsService.getUserDashboard(1L);
        
        assertNotNull(dashboard);
        assertTrue(dashboard.containsKey("activeAlerts"));
    }

    @Test
    @DisplayName("Test getUserDashboard contains totalAlerts")
    void testUserDashboardHasTotalAlerts() {
        Map<String, Object> dashboard = analyticsService.getUserDashboard(1L);
        
        assertNotNull(dashboard);
        assertTrue(dashboard.containsKey("totalAlerts"));
    }

    @Test
    @DisplayName("Test getUserDashboard contains totalMatches")
    void testUserDashboardHasMatches() {
        Map<String, Object> dashboard = analyticsService.getUserDashboard(1L);
        
        assertNotNull(dashboard);
        assertTrue(dashboard.containsKey("totalMatches"));
    }

    @Test
    @DisplayName("Test getUserDashboard contains averageMatchScore")
    void testUserDashboardHasAverageScore() {
        Map<String, Object> dashboard = analyticsService.getUserDashboard(1L);
        
        assertNotNull(dashboard);
        assertTrue(dashboard.containsKey("averageMatchScore"));
    }

    @Test
    @DisplayName("Test getAdminDashboard returns map")
    void testGetAdminDashboard() {
        when(userRepository.count()).thenReturn(100L);
        when(jobAlertRepository.count()).thenReturn(500L);
        when(jobMatchRepository.count()).thenReturn(5000L);
        when(notificationPreferenceRepository.countEmailEnabledUsers()).thenReturn(80L);
        when(notificationPreferenceRepository.findUsersWithWeeklyDigestEnabled()).thenReturn(new ArrayList<>());
        
        Map<String, Object> adminDashboard = analyticsService.getAdminDashboard();
        
        assertNotNull(adminDashboard);
        assertFalse(adminDashboard.isEmpty());
    }

    @Test
    @DisplayName("Test getAdminDashboard contains totalUsers")
    void testAdminDashboardHasTotalUsers() {
        when(userRepository.count()).thenReturn(100L);
        when(jobAlertRepository.count()).thenReturn(500L);
        when(jobMatchRepository.count()).thenReturn(5000L);
        when(notificationPreferenceRepository.countEmailEnabledUsers()).thenReturn(80L);
        when(notificationPreferenceRepository.findUsersWithWeeklyDigestEnabled()).thenReturn(new ArrayList<>());
        
        Map<String, Object> adminDashboard = analyticsService.getAdminDashboard();
        
        assertNotNull(adminDashboard);
        assertTrue(adminDashboard.containsKey("totalUsers"));
    }

    @Test
    @DisplayName("Test getMatchQualityDistribution returns map")
    void testGetMatchQualityDistribution() {
        Map<String, Long> distribution = analyticsService.getMatchQualityDistribution(1L);
        
        assertNotNull(distribution);
        assertFalse(distribution.isEmpty());
    }

    @Test
    @DisplayName("Test getMatchQualityDistribution has excellent key")
    void testQualityDistributionHasExcellent() {
        Map<String, Long> distribution = analyticsService.getMatchQualityDistribution(1L);
        
        assertNotNull(distribution);
        assertTrue(distribution.containsKey("excellent"));
    }

    @Test
    @DisplayName("Test getMatchQualityDistribution has all levels")
    void testQualityDistributionHasAllLevels() {
        Map<String, Long> distribution = analyticsService.getMatchQualityDistribution(1L);
        
        assertNotNull(distribution);
        assertTrue(distribution.containsKey("excellent"));
        assertTrue(distribution.containsKey("veryGood"));
        assertTrue(distribution.containsKey("good"));
        assertTrue(distribution.containsKey("fair"));
        assertTrue(distribution.containsKey("poor"));
    }

    @Test
    @DisplayName("Test getTopJobTitles returns list")
    void testGetTopJobTitles() {
        when(jobAlertRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        
        List<Map<String, Object>> topTitles = analyticsService.getTopJobTitles(1L, 10);
        
        assertNotNull(topTitles);
        assertTrue(topTitles instanceof List);
    }

    @Test
    @DisplayName("Test getMatchTrend returns list with 30 days")
    void testGetMatchTrend() {
        List<Map<String, Object>> trend = analyticsService.getMatchTrend(1L);
        
        assertNotNull(trend);
        assertTrue(trend.size() > 0);
    }

    @Test
    @DisplayName("Test getMatchTrend has date key")
    void testGetMatchTrendHasDate() {
        List<Map<String, Object>> trend = analyticsService.getMatchTrend(1L);
        
        assertNotNull(trend);
        if (!trend.isEmpty()) {
            assertTrue(trend.get(0).containsKey("date"));
        }
    }

    @Test
    @DisplayName("Test getMatchTrend has matchCount key")
    void testGetMatchTrendHasCount() {
        List<Map<String, Object>> trend = analyticsService.getMatchTrend(1L);
        
        assertNotNull(trend);
        if (!trend.isEmpty()) {
            assertTrue(trend.get(0).containsKey("matchCount"));
        }
    }

    @Test
    @DisplayName("Test getSkillAnalytics returns map")
    void testGetSkillAnalytics() {
        Map<String, Object> skillAnalytics = analyticsService.getSkillAnalytics(1L);
        
        assertNotNull(skillAnalytics);
        assertFalse(skillAnalytics.isEmpty());
    }

    @Test
    @DisplayName("Test getSkillAnalytics has topSkills")
    void testSkillAnalyticsHasTopSkills() {
        Map<String, Object> skillAnalytics = analyticsService.getSkillAnalytics(1L);
        
        assertNotNull(skillAnalytics);
        assertTrue(skillAnalytics.containsKey("topSkills"));
    }

    @Test
    @DisplayName("Test getSkillAnalytics has totalUniqueSkills")
    void testSkillAnalyticsHasTotalSkills() {
        Map<String, Object> skillAnalytics = analyticsService.getSkillAnalytics(1L);
        
        assertNotNull(skillAnalytics);
        assertTrue(skillAnalytics.containsKey("totalUniqueSkills"));
    }

    @Test
    @DisplayName("Test getMonthlyReport returns map")
    void testGetMonthlyReport() {
        when(jobAlertRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        when(jobMatchRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        
        Map<String, Object> report = analyticsService.getMonthlyReport(1L, YearMonth.now());
        
        assertNotNull(report);
        assertTrue(report instanceof Map);
    }

    @Test
    @DisplayName("Test quality distribution all values are non-negative")
    void testQualityDistributionNonNegative() {
        Map<String, Long> distribution = analyticsService.getMatchQualityDistribution(1L);
        
        assertNotNull(distribution);
        for (Long value : distribution.values()) {
            assertTrue(value >= 0);
        }
    }

    @Test
    @DisplayName("Test match trend returns at least 30 entries")
    void testMatchTrendMinimumEntries() {
        List<Map<String, Object>> trend = analyticsService.getMatchTrend(1L);
        
        assertNotNull(trend);
        assertTrue(trend.size() >= 30);
    }

    @Test
    @DisplayName("Test skill analytics total skills is non-negative")
    void testSkillAnalyticsTotalNonNegative() {
        Map<String, Object> skillAnalytics = analyticsService.getSkillAnalytics(1L);
        
        assertNotNull(skillAnalytics);
        Number totalSkills = (Number) skillAnalytics.get("totalUniqueSkills");
        assertTrue(totalSkills.longValue() >= 0);
    }
}
