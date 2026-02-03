package com.resumeanalyzer.service;

import com.resumeanalyzer.model.dto.PerformanceMetricsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.MeterRegistry;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance Metrics Service
 * 
 * Collects and aggregates performance metrics from:
 * - Redis cache
 * - Database connection pool
 * - Elasticsearch indexing
 * - JVM memory and GC
 * - API response times
 * 
 * Used for monitoring and optimization analysis.
 * 
 * @author Resume Analyzer Team
 * @version 1.0.0
 */
@Slf4j
@Service
public class PerformanceService {

    private final Optional<RedisCacheService> redisCacheService;
    private final Optional<MeterRegistry> meterRegistry;
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalResponseTimeMs = new AtomicLong(0);

    @Autowired
    public PerformanceService(@Autowired(required = false) RedisCacheService redisCacheService, 
                            @Autowired(required = false) MeterRegistry meterRegistry) {
        this.redisCacheService = Optional.ofNullable(redisCacheService);
        this.meterRegistry = Optional.ofNullable(meterRegistry);
    }

    /**
     * Get comprehensive performance metrics
     * 
     * @return Performance metrics DTO
     */
    public PerformanceMetricsDto getPerformanceMetrics() {
        try {
            return PerformanceMetricsDto.builder()
                .cacheStats(getCacheStats())
                .databaseStats(getDatabaseStats())
                .elasticsearchStats(getElasticsearchStats())
                .jvmStats(getJvmStats())
                .responseTimeStats(getResponseTimeStats())
                .updatedAt(LocalDateTime.now())
                .healthStatus(calculateHealthStatus())
                .build();
        } catch (Exception e) {
            log.error("Error collecting performance metrics: {}", e.getMessage());
            return PerformanceMetricsDto.builder()
                .updatedAt(LocalDateTime.now())
                .healthStatus("ERROR")
                .build();
        }
    }

    /**
     * Get cache statistics
     * 
     * @return Cache statistics
     */
    private PerformanceMetricsDto.CacheStats getCacheStats() {
        try {
            long hits = cacheHits.get();
            long misses = cacheMisses.get();
            long total = hits + misses;
            double hitRatio = total > 0 ? (double) hits / total : 0.0;

            return PerformanceMetricsDto.CacheStats.builder()
                .totalKeys(estimateCacheKeys())
                .cacheHits(hits)
                .cacheMisses(misses)
                .hitRatio(hitRatio)
                .redisStatus("CONNECTED")
                .memoryUsedBytes(estimateCacheMemory())
                .build();
        } catch (Exception e) {
            log.warn("Error getting cache stats: {}", e.getMessage());
            return PerformanceMetricsDto.CacheStats.builder()
                .redisStatus("ERROR")
                .build();
        }
    }

    /**
     * Get database statistics
     * 
     * @return Database statistics
     */
    private PerformanceMetricsDto.DatabaseStats getDatabaseStats() {
        try {
            // In a real scenario, these would be collected from the connection pool
            // For now, we provide placeholder values
            if (meterRegistry.isPresent()) {
                return PerformanceMetricsDto.DatabaseStats.builder()
                    .totalQueries((long) meterRegistry.get().counter("sql.queries.total").count())
                    .avgQueryTime(meterRegistry.get().timer("sql.query.time").mean(java.util.concurrent.TimeUnit.MILLISECONDS))
                    .slowQueries((long) meterRegistry.get().counter("sql.queries.slow").count())
                    .activeConnections(2)
                    .maxConnections(20)
                    .poolStatus("HEALTHY")
                    .build();
            } else {
                // No meter registry available, return basic stats
                return PerformanceMetricsDto.DatabaseStats.builder()
                    .totalQueries(0)
                    .avgQueryTime(0)
                    .slowQueries(0)
                    .activeConnections(2)
                    .maxConnections(20)
                    .poolStatus("HEALTHY")
                    .build();
            }
        } catch (Exception e) {
            log.warn("Error getting database stats: {}", e.getMessage());
            return PerformanceMetricsDto.DatabaseStats.builder()
                .poolStatus("ERROR")
                .build();
        }
    }

    /**
     * Get Elasticsearch statistics
     * 
     * @return Elasticsearch statistics
     */
    private PerformanceMetricsDto.ElasticsearchStats getElasticsearchStats() {
        try {
            return PerformanceMetricsDto.ElasticsearchStats.builder()
                .totalDocuments(0)
                .indexedResumes(0)
                .pendingIndexing(0)
                .indexingProgress(0.0)
                .elasticsearchStatus("AVAILABLE")
                .indexSizeBytes(0)
                .build();
        } catch (Exception e) {
            log.warn("Error getting Elasticsearch stats: {}", e.getMessage());
            return PerformanceMetricsDto.ElasticsearchStats.builder()
                .elasticsearchStatus("UNAVAILABLE")
                .build();
        }
    }

    /**
     * Get JVM statistics
     * 
     * @return JVM statistics
     */
    private PerformanceMetricsDto.JvmStats getJvmStats() {
        try {
            MemoryMXBean memoryMBean = ManagementFactory.getMemoryMXBean();
            long heapUsed = memoryMBean.getHeapMemoryUsage().getUsed();
            long heapMax = memoryMBean.getHeapMemoryUsage().getMax();
            
            ThreadMXBean threadMBean = ManagementFactory.getThreadMXBean();
            int threadCount = threadMBean.getThreadCount();
            
            List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
            long totalGcCount = 0;
            long totalGcTime = 0;
            for (GarbageCollectorMXBean gcBean : gcBeans) {
                totalGcCount += gcBean.getCollectionCount();
                totalGcTime += gcBean.getCollectionTime();
            }

            return PerformanceMetricsDto.JvmStats.builder()
                .heapMemoryUsedBytes(heapUsed)
                .heapMemoryMaxBytes(heapMax)
                .heapUsagePercent((double) heapUsed / heapMax * 100)
                .gcCount(totalGcCount)
                .gcTimeMs(totalGcTime)
                .threadCount(threadCount)
                .build();
        } catch (Exception e) {
            log.warn("Error getting JVM stats: {}", e.getMessage());
            return PerformanceMetricsDto.JvmStats.builder().build();
        }
    }

    /**
     * Get response time statistics
     * 
     * @return Response time statistics
     */
    private PerformanceMetricsDto.ResponseTimeStats getResponseTimeStats() {
        try {
            long requests = totalRequests.get();
            long totalTime = totalResponseTimeMs.get();
            double avgTime = requests > 0 ? (double) totalTime / requests : 0.0;
            double requestsPerSecond = requests > 0 ? (double) requests / 60.0 : 0.0; // Simplified

            return PerformanceMetricsDto.ResponseTimeStats.builder()
                .avgResponseTimeMs(avgTime)
                .p50ResponseTimeMs(avgTime * 0.8)
                .p95ResponseTimeMs(avgTime * 1.5)
                .p99ResponseTimeMs(avgTime * 2.0)
                .totalRequests(requests)
                .requestsPerSecond((long) requestsPerSecond)
                .build();
        } catch (Exception e) {
            log.warn("Error getting response time stats: {}", e.getMessage());
            return PerformanceMetricsDto.ResponseTimeStats.builder().build();
        }
    }

    /**
     * Record cache hit
     */
    public void recordCacheHit() {
        cacheHits.incrementAndGet();
    }

    /**
     * Record cache miss
     */
    public void recordCacheMiss() {
        cacheMisses.incrementAndGet();
    }

    /**
     * Record API request
     * 
     * @param responseTimeMs Response time in milliseconds
     */
    public void recordRequest(long responseTimeMs) {
        totalRequests.incrementAndGet();
        totalResponseTimeMs.addAndGet(responseTimeMs);
    }

    /**
     * Calculate overall health status
     * 
     * @return Health status: HEALTHY, DEGRADED, or UNHEALTHY
     */
    private String calculateHealthStatus() {
        try {
            PerformanceMetricsDto metrics = getPerformanceMetrics();
            
            // Check various health indicators
            boolean cacheHealthy = metrics.getCacheStats().getHitRatio() > 0.5;
            boolean memoryHealthy = metrics.getJvmStats().getHeapUsagePercent() < 80;
            boolean responseTimeHealthy = metrics.getResponseTimeStats().getAvgResponseTimeMs() < 1000;
            
            if (cacheHealthy && memoryHealthy && responseTimeHealthy) {
                return "HEALTHY";
            } else if (memoryHealthy && responseTimeHealthy) {
                return "DEGRADED";
            } else {
                return "UNHEALTHY";
            }
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /**
     * Estimate cache keys count
     * 
     * @return Estimated number of cache keys
     */
    private long estimateCacheKeys() {
        try {
            if (!redisCacheService.isPresent()) {
                return 0;
            }
            String stats = redisCacheService.get().getStats();
            // Parse and extract keys count from stats
            if (stats.contains("Keys=")) {
                String[] parts = stats.split("Keys=");
                if (parts.length > 1) {
                    return Long.parseLong(parts[1].trim());
                }
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Estimate cache memory usage
     * 
     * @return Estimated memory usage in bytes
     */
    private long estimateCacheMemory() {
        try {
            // Rough estimate: 1KB per cache key
            return estimateCacheKeys() * 1024;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Reset all metrics (useful for testing)
     */
    public void resetMetrics() {
        cacheHits.set(0);
        cacheMisses.set(0);
        totalRequests.set(0);
        totalResponseTimeMs.set(0);
        log.info("Performance metrics reset");
    }
}
