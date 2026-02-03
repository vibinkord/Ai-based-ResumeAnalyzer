package com.resumeanalyzer.controller;

import com.resumeanalyzer.model.dto.PerformanceMetricsDto;
import com.resumeanalyzer.service.PerformanceService;
import com.resumeanalyzer.service.RedisCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Performance Metrics REST Controller
 * 
 * Provides endpoints for:
 * - Monitoring performance metrics
 * - Cache management
 * - Performance health status
 * - Performance optimization recommendations
 * 
 * All endpoints require ADMIN role for security.
 * 
 * @author Resume Analyzer Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/performance")
@Tag(name = "Performance", description = "Performance monitoring and optimization endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class PerformanceController {

    private final PerformanceService performanceService;
    private final Optional<RedisCacheService> cacheService;

    @Autowired
    public PerformanceController(PerformanceService performanceService,
                               @Autowired(required = false) RedisCacheService cacheService) {
        this.performanceService = performanceService;
        this.cacheService = Optional.ofNullable(cacheService);
    }

    /**
     * Get comprehensive performance metrics
     * 
     * @return Performance metrics
     */
    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get comprehensive performance metrics", description = "Returns cache, database, Elasticsearch, JVM, and response time metrics")
    public ResponseEntity<PerformanceMetricsDto> getPerformanceMetrics() {
        log.info("Getting performance metrics");
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get cache statistics
     * 
     * @return Cache statistics
     */
    @GetMapping("/cache-stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get cache statistics", description = "Returns Redis cache hit/miss ratios and memory usage")
    public ResponseEntity<PerformanceMetricsDto.CacheStats> getCacheStats() {
        log.info("Getting cache statistics");
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();
        return ResponseEntity.ok(metrics.getCacheStats());
    }

    /**
     * Get database performance statistics
     * 
     * @return Database statistics
     */
    @GetMapping("/database-stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get database statistics", description = "Returns query counts, average response time, and connection pool status")
    public ResponseEntity<PerformanceMetricsDto.DatabaseStats> getDatabaseStats() {
        log.info("Getting database statistics");
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();
        return ResponseEntity.ok(metrics.getDatabaseStats());
    }

    /**
     * Get Elasticsearch indexing statistics
     * 
     * @return Elasticsearch statistics
     */
    @GetMapping("/elasticsearch-stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get Elasticsearch statistics", description = "Returns indexing status, document count, and index size")
    public ResponseEntity<PerformanceMetricsDto.ElasticsearchStats> getElasticsearchStats() {
        log.info("Getting Elasticsearch statistics");
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();
        return ResponseEntity.ok(metrics.getElasticsearchStats());
    }

    /**
     * Get JVM statistics
     * 
     * @return JVM statistics
     */
    @GetMapping("/jvm-stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get JVM statistics", description = "Returns heap memory usage, garbage collection metrics, and thread count")
    public ResponseEntity<PerformanceMetricsDto.JvmStats> getJvmStats() {
        log.info("Getting JVM statistics");
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();
        return ResponseEntity.ok(metrics.getJvmStats());
    }

    /**
     * Get response time statistics
     * 
     * @return Response time statistics
     */
    @GetMapping("/response-time-stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get response time statistics", description = "Returns average, p50, p95, p99 response times and requests per second")
    public ResponseEntity<PerformanceMetricsDto.ResponseTimeStats> getResponseTimeStats() {
        log.info("Getting response time statistics");
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();
        return ResponseEntity.ok(metrics.getResponseTimeStats());
    }

    /**
     * Get health status
     * 
     * @return Health status
     */
    @GetMapping("/health-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get system health status", description = "Returns overall system health: HEALTHY, DEGRADED, or UNHEALTHY")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        log.info("Getting system health status");
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();
        Map<String, Object> response = new HashMap<>();
        response.put("status", metrics.getHealthStatus());
        response.put("timestamp", metrics.getUpdatedAt());
        return ResponseEntity.ok(response);
    }

    /**
     * Clear Redis cache
     * 
     * @return Confirmation message
     */
    @PostMapping("/cache/clear")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Clear all Redis cache", description = "Clears all entries from Redis cache (use with caution)")
    public ResponseEntity<Map<String, String>> clearCache() {
        log.warn("Clearing all cache entries");
        if (cacheService.isPresent()) {
            cacheService.get().clearAll();
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cache cleared successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Clear cache entries by pattern
     * 
     * @param pattern Key pattern (e.g., "user:*")
     * @return Confirmation message
     */
    @PostMapping("/cache/clear-pattern")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Clear cache by pattern", description = "Clears cache entries matching a pattern")
    public ResponseEntity<Map<String, String>> clearCacheByPattern(
            @RequestParam String pattern) {
        log.warn("Clearing cache entries matching pattern: {}", pattern);
        if (cacheService.isPresent()) {
            cacheService.get().deleteByPattern(pattern);
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cache entries cleared for pattern: " + pattern);
        return ResponseEntity.ok(response);
    }

    /**
     * Get performance optimization recommendations
     * 
     * @return Recommendations
     */
    @GetMapping("/recommendations")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get optimization recommendations", description = "Analyzes performance metrics and returns optimization suggestions")
    public ResponseEntity<Map<String, Object>> getRecommendations() {
        log.info("Generating performance optimization recommendations");
        PerformanceMetricsDto metrics = performanceService.getPerformanceMetrics();
        Map<String, Object> recommendations = new HashMap<>();
        
        // Analyze metrics and generate recommendations
        if (metrics.getCacheStats() != null) {
            if (metrics.getCacheStats().getHitRatio() < 0.5) {
                recommendations.put("cache_optimization", "Consider increasing cache TTL or implementing cache warming");
            }
        }
        
        if (metrics.getJvmStats() != null) {
            if (metrics.getJvmStats().getHeapUsagePercent() > 80) {
                recommendations.put("memory_optimization", "Heap memory usage is high, consider increasing max heap size");
            }
        }
        
        if (metrics.getResponseTimeStats() != null) {
            if (metrics.getResponseTimeStats().getAvgResponseTimeMs() > 1000) {
                recommendations.put("query_optimization", "Response times are high, consider database query optimization");
            }
        }
        
        if (recommendations.isEmpty()) {
            recommendations.put("status", "System is performing optimally");
        }
        
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get cache information
     * 
     * @return Cache information
     */
    @GetMapping("/cache-info")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get cache information", description = "Returns detailed cache statistics and information")
    public ResponseEntity<Map<String, String>> getCacheInfo() {
        log.info("Getting cache information");
        Map<String, String> info = new HashMap<>();
        if (cacheService.isPresent()) {
            info.put("cache_status", cacheService.get().getStats());
        } else {
            info.put("cache_status", "Redis cache not available");
        }
        return ResponseEntity.ok(info);
    }

    /**
     * Reset performance metrics (for testing)
     * 
     * @return Confirmation message
     */
    @PostMapping("/reset-metrics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reset performance metrics", description = "Resets all performance metrics (testing only)")
    public ResponseEntity<Map<String, String>> resetMetrics() {
        log.info("Resetting performance metrics");
        performanceService.resetMetrics();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Performance metrics reset successfully");
        return ResponseEntity.ok(response);
    }
}
