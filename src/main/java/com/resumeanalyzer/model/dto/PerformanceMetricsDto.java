package com.resumeanalyzer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * DTO for Performance Metrics and Optimization Status
 * 
 * Provides insights into:
 * - Cache hit/miss ratios
 * - Database query performance
 * - Elasticsearch indexing status
 * - Memory usage
 * - Response time metrics
 * 
 * @author Resume Analyzer Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceMetricsDto {

    /**
     * Cache Statistics
     */
    @JsonProperty("cache_stats")
    private CacheStats cacheStats;

    /**
     * Database Performance Metrics
     */
    @JsonProperty("database_stats")
    private DatabaseStats databaseStats;

    /**
     * Elasticsearch Indexing Status
     */
    @JsonProperty("elasticsearch_stats")
    private ElasticsearchStats elasticsearchStats;

    /**
     * Memory and JVM Metrics
     */
    @JsonProperty("jvm_stats")
    private JvmStats jvmStats;

    /**
     * API Response Time Metrics
     */
    @JsonProperty("response_time_stats")
    private ResponseTimeStats responseTimeStats;

    /**
     * Last update time
     */
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    /**
     * Overall health status
     */
    @JsonProperty("health_status")
    private String healthStatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CacheStats {
        private long totalKeys;
        private long cacheHits;
        private long cacheMisses;
        private double hitRatio;
        private String redisStatus;
        private long memoryUsedBytes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DatabaseStats {
        private long totalQueries;
        private double avgQueryTime;
        private long slowQueries;
        private int activeConnections;
        private int maxConnections;
        private String poolStatus;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ElasticsearchStats {
        private long totalDocuments;
        private long indexedResumes;
        private long pendingIndexing;
        private double indexingProgress;
        private String elasticsearchStatus;
        private long indexSizeBytes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JvmStats {
        private long heapMemoryUsedBytes;
        private long heapMemoryMaxBytes;
        private double heapUsagePercent;
        private long gcCount;
        private long gcTimeMs;
        private int threadCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponseTimeStats {
        private double avgResponseTimeMs;
        private double p50ResponseTimeMs;
        private double p95ResponseTimeMs;
        private double p99ResponseTimeMs;
        private long totalRequests;
        private long requestsPerSecond;
    }
}
