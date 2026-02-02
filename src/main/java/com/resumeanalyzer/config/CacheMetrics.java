package com.resumeanalyzer.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache metrics tracking service.
 * 
 * Monitors and records cache performance metrics including:
 * - Cache hits and misses
 * - Cache evictions
 * - Cache operation timings
 */
public class CacheMetrics {
    private static final Logger log = LoggerFactory.getLogger(CacheMetrics.class);
    private final MeterRegistry meterRegistry;

    public CacheMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Record a cache hit.
     */
    public void recordCacheHit(String cacheName) {
        meterRegistry.counter(
            "cache.hits",
            Tags.of(Tag.of("cache", cacheName))
        ).increment();
        log.debug("Cache hit for: {}", cacheName);
    }

    /**
     * Record a cache miss.
     */
    public void recordCacheMiss(String cacheName) {
        meterRegistry.counter(
            "cache.misses",
            Tags.of(Tag.of("cache", cacheName))
        ).increment();
        log.debug("Cache miss for: {}", cacheName);
    }

    /**
     * Record cache operation duration.
     */
    public void recordCacheOperationDuration(String cacheName, long durationMs) {
        meterRegistry.timer(
            "cache.operation.duration",
            Tags.of(Tag.of("cache", cacheName))
        ).record(durationMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
}
