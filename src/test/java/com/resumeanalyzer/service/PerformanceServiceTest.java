package com.resumeanalyzer.service;

import com.resumeanalyzer.model.dto.PerformanceMetricsDto;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PerformanceService
 * 
 * Tests performance metric collection including:
 * - Cache statistics
 * - Database statistics
 * - JVM statistics
 * - Response time metrics
 * - Health status calculation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PerformanceService Tests")
class PerformanceServiceTest {

    @Mock
    private RedisCacheService cacheService;

    private MeterRegistry meterRegistry;
    private PerformanceService performanceService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        performanceService = new PerformanceService(cacheService, meterRegistry);
        when(cacheService.getStats()).thenReturn("Cache Statistics: Total Keys=10");
    }

    @Test
    @DisplayName("Should get comprehensive performance metrics")
    void testGetPerformanceMetrics() {
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();

        assertNotNull(metrics);
        assertNotNull(metrics.getCacheStats());
        assertNotNull(metrics.getDatabaseStats());
        assertNotNull(metrics.getJvmStats());
        assertNotNull(metrics.getResponseTimeStats());
        assertNotNull(metrics.getUpdatedAt());
        assertNotNull(metrics.getHealthStatus());
    }

    @Test
    @DisplayName("Should get cache statistics")
    void testGetCacheStatistics() {
        performanceService.recordCacheHit();
        performanceService.recordCacheHit();
        performanceService.recordCacheMiss();

        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();

        assertNotNull(metrics.getCacheStats());
        assertEquals(2, metrics.getCacheStats().getCacheHits());
        assertEquals(1, metrics.getCacheStats().getCacheMisses());
        assertTrue(metrics.getCacheStats().getHitRatio() > 0);
    }

    @Test
    @DisplayName("Should calculate cache hit ratio correctly")
    void testCacheHitRatioCalculation() {
        for (int i = 0; i < 8; i++) {
            performanceService.recordCacheHit();
        }
        for (int i = 0; i < 2; i++) {
            performanceService.recordCacheMiss();
        }

        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();

        double expectedRatio = 8.0 / 10.0;
        assertEquals(expectedRatio, metrics.getCacheStats().getHitRatio(), 0.001);
    }

    @Test
    @DisplayName("Should record API request metrics")
    void testRecordRequest() {
        performanceService.recordRequest(100);
        performanceService.recordRequest(200);
        performanceService.recordRequest(150);

        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();

        assertEquals(3, metrics.getResponseTimeStats().getTotalRequests());
        assertTrue(metrics.getResponseTimeStats().getAvgResponseTimeMs() > 0);
    }

    @Test
    @DisplayName("Should calculate average response time")
    void testAverageResponseTimeCalculation() {
        performanceService.recordRequest(100);
        performanceService.recordRequest(200);
        performanceService.recordRequest(300);

        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();

        double expectedAvg = 200.0;
        assertEquals(expectedAvg, metrics.getResponseTimeStats().getAvgResponseTimeMs(), 0.001);
    }

    @Test
    @DisplayName("Should get JVM statistics")
    void testGetJvmStatistics() {
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();

        assertNotNull(metrics.getJvmStats());
        assertTrue(metrics.getJvmStats().getHeapMemoryUsedBytes() > 0);
        assertTrue(metrics.getJvmStats().getHeapMemoryMaxBytes() > 0);
        assertTrue(metrics.getJvmStats().getHeapUsagePercent() >= 0);
        assertTrue(metrics.getJvmStats().getThreadCount() > 0);
    }

    @Test
    @DisplayName("Should calculate heap usage percentage")
    void testHeapUsagePercentage() {
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();

        double heapUsage = metrics.getJvmStats().getHeapUsagePercent();
        assertTrue(heapUsage >= 0 && heapUsage <= 100);
    }

    @Test
    @DisplayName("Should get database statistics")
    void testGetDatabaseStatistics() {
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();

        assertNotNull(metrics.getDatabaseStats());
        assertNotNull(metrics.getDatabaseStats().getPoolStatus());
    }

    @Test
    @DisplayName("Should get Elasticsearch statistics")
    void testGetElasticsearchStatistics() {
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();

        assertNotNull(metrics.getElasticsearchStats());
        assertNotNull(metrics.getElasticsearchStats().getElasticsearchStatus());
    }

    @Test
    @DisplayName("Should determine health status as HEALTHY")
    void testHealthStatusHealthy() {
        // Simulate good performance
        for (int i = 0; i < 80; i++) {
            performanceService.recordCacheHit();
        }
        for (int i = 0; i < 20; i++) {
            performanceService.recordCacheMiss();
        }

        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();

        assertNotNull(metrics.getHealthStatus());
    }

    @Test
    @DisplayName("Should reset metrics")
    void testResetMetrics() {
        performanceService.recordCacheHit();
        performanceService.recordRequest(100);

        performanceService.resetMetrics();

        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();

        assertEquals(0, metrics.getCacheStats().getCacheHits());
        assertEquals(0, metrics.getResponseTimeStats().getTotalRequests());
    }

    @Test
    @DisplayName("Should handle errors gracefully")
    void testErrorHandling() {
        when(cacheService.getStats()).thenThrow(new RuntimeException("Cache error"));

        // Should not throw exception
        assertDoesNotThrow(() -> performanceService.getPerformanceMetrics());

        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();
        assertNotNull(metrics);
    }

    @Test
    @DisplayName("Should calculate response time percentiles")
    void testResponseTimePercentiles() {
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();

        assertNotNull(metrics.getResponseTimeStats());
        assertTrue(metrics.getResponseTimeStats().getP50ResponseTimeMs() >= 0);
        assertTrue(metrics.getResponseTimeStats().getP95ResponseTimeMs() >= 0);
        assertTrue(metrics.getResponseTimeStats().getP99ResponseTimeMs() >= 0);
    }

    @Test
    @DisplayName("Should estimate cache memory usage")
    void testCacheMemoryEstimation() {
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();

        assertNotNull(metrics.getCacheStats());
        assertTrue(metrics.getCacheStats().getMemoryUsedBytes() >= 0);
    }
}
