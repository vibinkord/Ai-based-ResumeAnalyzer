package com.resumeanalyzer.controller;

import com.resumeanalyzer.model.dto.PerformanceMetricsDto;
import com.resumeanalyzer.service.PerformanceService;
import com.resumeanalyzer.service.RedisCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PerformanceController
 * 
 * Tests REST endpoints for:
 * - Performance metrics retrieval
 * - Cache management
 * - Health status
 * - Optimization recommendations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PerformanceController Tests")
class PerformanceControllerTest {

    @Mock
    private PerformanceService performanceService;

    @Mock
    private RedisCacheService cacheService;

    private MockMvc mockMvc;
    private PerformanceController controller;

    @BeforeEach
    void setUp() {
        controller = new PerformanceController(performanceService, cacheService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Should get performance metrics without authentication")
    void testGetPerformanceMetricsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/performance/metrics"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get performance metrics with ADMIN role")
    void testGetPerformanceMetricsWithAdmin() throws Exception {
        PerformanceMetricsDto metricsDto = PerformanceMetricsDto.builder()
            .healthStatus("HEALTHY")
            .updatedAt(LocalDateTime.now())
            .cacheStats(PerformanceMetricsDto.CacheStats.builder()
                .hitRatio(0.8)
                .totalKeys(100)
                .build())
            .build();

        when(performanceService.getPerformanceMetrics()).thenReturn(metricsDto);

        mockMvc.perform(get("/api/performance/metrics")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.health_status").value("HEALTHY"))
            .andExpect(jsonPath("$.cache_stats.hit_ratio").value(0.8));

        verify(performanceService).getPerformanceMetrics();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny performance metrics to non-admin users")
    void testGetPerformanceMetricsDeniedToUser() throws Exception {
        mockMvc.perform(get("/api/performance/metrics"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get cache statistics")
    void testGetCacheStats() throws Exception {
        PerformanceMetricsDto.CacheStats cacheStats = PerformanceMetricsDto.CacheStats.builder()
            .hitRatio(0.85)
            .totalKeys(500)
            .redisStatus("CONNECTED")
            .build();

        PerformanceMetricsDto metricsDto = PerformanceMetricsDto.builder()
            .cacheStats(cacheStats)
            .build();

        when(performanceService.getPerformanceMetrics()).thenReturn(metricsDto);

        mockMvc.perform(get("/api/performance/cache-stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hit_ratio").value(0.85))
            .andExpect(jsonPath("$.redis_status").value("CONNECTED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get database statistics")
    void testGetDatabaseStats() throws Exception {
        PerformanceMetricsDto.DatabaseStats dbStats = PerformanceMetricsDto.DatabaseStats.builder()
            .totalQueries(1000)
            .avgQueryTime(50.5)
            .poolStatus("HEALTHY")
            .build();

        PerformanceMetricsDto metricsDto = PerformanceMetricsDto.builder()
            .databaseStats(dbStats)
            .build();

        when(performanceService.getPerformanceMetrics()).thenReturn(metricsDto);

        mockMvc.perform(get("/api/performance/database-stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total_queries").value(1000))
            .andExpect(jsonPath("$.pool_status").value("HEALTHY"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get JVM statistics")
    void testGetJvmStats() throws Exception {
        PerformanceMetricsDto.JvmStats jvmStats = PerformanceMetricsDto.JvmStats.builder()
            .heapUsagePercent(65.5)
            .threadCount(42)
            .build();

        PerformanceMetricsDto metricsDto = PerformanceMetricsDto.builder()
            .jvmStats(jvmStats)
            .build();

        when(performanceService.getPerformanceMetrics()).thenReturn(metricsDto);

        mockMvc.perform(get("/api/performance/jvm-stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.heap_usage_percent").value(65.5))
            .andExpect(jsonPath("$.thread_count").value(42));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get health status")
    void testGetHealthStatus() throws Exception {
        PerformanceMetricsDto metricsDto = PerformanceMetricsDto.builder()
            .healthStatus("HEALTHY")
            .updatedAt(LocalDateTime.now())
            .build();

        when(performanceService.getPerformanceMetrics()).thenReturn(metricsDto);

        mockMvc.perform(get("/api/performance/health-status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("HEALTHY"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should clear cache")
    void testClearCache() throws Exception {
        mockMvc.perform(post("/api/performance/cache/clear")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", containsString("Cache cleared")));

        verify(cacheService).clearAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should clear cache by pattern")
    void testClearCacheByPattern() throws Exception {
        mockMvc.perform(post("/api/performance/cache/clear-pattern")
            .param("pattern", "user:*")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", containsString("user:*")));

        verify(cacheService).deleteByPattern("user:*");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get optimization recommendations")
    void testGetRecommendations() throws Exception {
        PerformanceMetricsDto metricsDto = PerformanceMetricsDto.builder()
            .cacheStats(PerformanceMetricsDto.CacheStats.builder()
                .hitRatio(0.9)
                .build())
            .jvmStats(PerformanceMetricsDto.JvmStats.builder()
                .heapUsagePercent(50.0)
                .build())
            .responseTimeStats(PerformanceMetricsDto.ResponseTimeStats.builder()
                .avgResponseTimeMs(500.0)
                .build())
            .build();

        when(performanceService.getPerformanceMetrics()).thenReturn(metricsDto);

        mockMvc.perform(get("/api/performance/recommendations"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get cache information")
    void testGetCacheInfo() throws Exception {
        when(cacheService.getStats()).thenReturn("Cache Statistics: Total Keys=100");

        mockMvc.perform(get("/api/performance/cache-info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cache_status", containsString("Cache Statistics")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should reset metrics")
    void testResetMetrics() throws Exception {
        mockMvc.perform(post("/api/performance/reset-metrics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message", containsString("reset")));

        verify(performanceService).resetMetrics();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny cache clear to non-admin users")
    void testClearCacheDeniedToUser() throws Exception {
        mockMvc.perform(post("/api/performance/cache/clear"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get response time statistics")
    void testGetResponseTimeStats() throws Exception {
        PerformanceMetricsDto.ResponseTimeStats rtStats = PerformanceMetricsDto.ResponseTimeStats.builder()
            .avgResponseTimeMs(250.5)
            .p95ResponseTimeMs(500.0)
            .p99ResponseTimeMs(750.0)
            .requestsPerSecond(100.0)
            .build();

        PerformanceMetricsDto metricsDto = PerformanceMetricsDto.builder()
            .responseTimeStats(rtStats)
            .build();

        when(performanceService.getPerformanceMetrics()).thenReturn(metricsDto);

        mockMvc.perform(get("/api/performance/response-time-stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.avg_response_time_ms").value(250.5))
            .andExpect(jsonPath("$.p95_response_time_ms").value(500.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get Elasticsearch statistics")
    void testGetElasticsearchStats() throws Exception {
        PerformanceMetricsDto.ElasticsearchStats esStats = PerformanceMetricsDto.ElasticsearchStats.builder()
            .totalDocuments(5000)
            .indexedResumes(4500)
            .elasticsearchStatus("AVAILABLE")
            .build();

        PerformanceMetricsDto metricsDto = PerformanceMetricsDto.builder()
            .elasticsearchStats(esStats)
            .build();

        when(performanceService.getPerformanceMetrics()).thenReturn(metricsDto);

        mockMvc.perform(get("/api/performance/elasticsearch-stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total_documents").value(5000))
            .andExpect(jsonPath("$.elasticsearch_status").value("AVAILABLE"));
    }
}
