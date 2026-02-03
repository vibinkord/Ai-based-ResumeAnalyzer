package com.resumeanalyzer.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Cache configuration for Resume Analyzer.
 * 
 * This configuration supports both in-memory caching (for development/testing)
 * and Redis-based distributed caching (for production).
 * 
 * The cache is enabled with @EnableCaching and can be switched between
 * implementations using Spring profiles (dev, test, prod).
 * 
 * Cache TTL (Time-To-Live) configuration:
 * - Skills cache: 1 hour
 * - Analysis results cache: 2 hours
 * - Job descriptions cache: 24 hours
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Development cache manager using in-memory concurrent map.
     * Fast, suitable for local development and testing.
     */
    @Bean
    @Profile({"dev", "test", "default"})
    @Primary
    public CacheManager devCacheManager() {
        return new ConcurrentMapCacheManager(
            "skills",
            "analysis-results",
            "job-descriptions",
            "resume-suggestions",
            "skill-matches",
            "all-skills",
            "skill-count"
        );
    }

    /**
     * Redis cache manager for production.
     * Provides distributed caching across multiple instances.
     */
    @Bean
    @Profile("prod")
    @Primary
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(2))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();

        return RedisCacheManager.create(connectionFactory);
    }

    /**
     * Redis connection factory for distributed caching.
     * Uses Lettuce as the Redis driver for connection management.
     */
    @Bean
    @ConditionalOnProperty(
        name = "spring.data.redis.host",
        matchIfMissing = false
    )
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    /**
     * Cache metrics bean for monitoring cache performance.
     * Tracks cache hits, misses, and evictions.
     * Only created if MeterRegistry is available (when actuator is enabled).
     */
    @Bean
    @ConditionalOnBean(MeterRegistry.class)
    public CacheMetrics cacheMetrics(MeterRegistry meterRegistry) {
        return new CacheMetrics(meterRegistry);
    }
}
