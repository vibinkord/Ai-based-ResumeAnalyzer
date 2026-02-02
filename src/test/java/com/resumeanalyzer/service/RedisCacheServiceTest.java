package com.resumeanalyzer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RedisCacheService
 * 
 * Tests cache operations including:
 * - Get/Set operations
 * - Type-safe retrieval
 * - TTL management
 * - Pattern-based deletion
 * - Error handling
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RedisCacheService Tests")
class RedisCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private RedisCacheService cacheService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        cacheService = new RedisCacheService(redisTemplate);
    }

    @Test
    @DisplayName("Should get cached value successfully")
    void testGetCachedValue() {
        String key = "test:key";
        String expectedValue = "test-value";
        when(valueOperations.get(key)).thenReturn(expectedValue);

        Optional<Object> result = cacheService.get(key);

        assertTrue(result.isPresent());
        assertEquals(expectedValue, result.get());
        verify(valueOperations).get(key);
    }

    @Test
    @DisplayName("Should return empty Optional when key not found")
    void testGetMissingKey() {
        String key = "missing:key";
        when(valueOperations.get(key)).thenReturn(null);

        Optional<Object> result = cacheService.get(key);

        assertFalse(result.isPresent());
        verify(valueOperations).get(key);
    }

    @Test
    @DisplayName("Should get typed cached value")
    void testGetTypedValue() {
        String key = "user:123";
        String expectedValue = "john-doe";
        when(valueOperations.get(key)).thenReturn(expectedValue);

        Optional<String> result = cacheService.get(key, String.class);

        assertTrue(result.isPresent());
        assertEquals(expectedValue, result.get());
    }

    @Test
    @DisplayName("Should return empty Optional for type mismatch")
    void testGetTypedValueMismatch() {
        String key = "user:123";
        when(valueOperations.get(key)).thenReturn("string-value");

        Optional<Integer> result = cacheService.get(key, Integer.class);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should set cache value with default TTL")
    void testSetValueWithDefaultTtl() {
        String key = "test:key";
        String value = "test-value";

        cacheService.set(key, value);

        verify(valueOperations).set(key, value, 1, TimeUnit.HOURS);
    }

    @Test
    @DisplayName("Should set cache value with custom TTL")
    void testSetValueWithCustomTtl() {
        String key = "test:key";
        String value = "test-value";

        cacheService.set(key, value, 30, TimeUnit.MINUTES);

        verify(valueOperations).set(key, value, 30, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("Should check if key exists")
    void testHasKey() {
        String key = "test:key";
        when(redisTemplate.hasKey(key)).thenReturn(true);

        boolean exists = cacheService.hasKey(key);

        assertTrue(exists);
        verify(redisTemplate).hasKey(key);
    }

    @Test
    @DisplayName("Should return false when key does not exist")
    void testHasKeyNotExists() {
        String key = "missing:key";
        when(redisTemplate.hasKey(key)).thenReturn(false);

        boolean exists = cacheService.hasKey(key);

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should delete cache entry")
    void testDeleteKey() {
        String key = "test:key";

        cacheService.delete(key);

        verify(redisTemplate).delete(key);
    }

    @Test
    @DisplayName("Should delete multiple cache entries")
    void testDeleteMultipleKeys() {
        String key1 = "test:key1";
        String key2 = "test:key2";
        String key3 = "test:key3";

        cacheService.deleteAll(key1, key2, key3);

        verify(redisTemplate, times(3)).delete(anyString());
    }

    @Test
    @DisplayName("Should expire cache entry")
    void testExpireKey() {
        String key = "test:key";
        when(redisTemplate.expire(key, 30, TimeUnit.MINUTES)).thenReturn(true);

        boolean result = cacheService.expire(key, 30, TimeUnit.MINUTES);

        assertTrue(result);
        verify(redisTemplate).expire(key, 30, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("Should get remaining TTL")
    void testGetExpire() {
        String key = "test:key";
        when(redisTemplate.getExpire(key, TimeUnit.SECONDS)).thenReturn(300L);

        long ttl = cacheService.getExpire(key);

        assertEquals(300L, ttl);
    }

    @Test
    @DisplayName("Should increment numeric value")
    void testIncrement() {
        String key = "counter:test";
        when(valueOperations.increment(key, 1)).thenReturn(2L);

        long result = cacheService.increment(key, 1);

        assertEquals(2L, result);
        verify(valueOperations).increment(key, 1);
    }

    @Test
    @DisplayName("Should decrement numeric value")
    void testDecrement() {
        String key = "counter:test";
        when(valueOperations.increment(key, -1)).thenReturn(1L);

        long result = cacheService.decrement(key, 1);

        assertEquals(1L, result);
    }

    @Test
    @DisplayName("Should handle errors gracefully")
    void testErrorHandling() {
        String key = "test:key";
        when(valueOperations.get(key)).thenThrow(new RuntimeException("Redis error"));

        Optional<Object> result = cacheService.get(key);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should get cache statistics")
    void testGetStats() {
        String stats = cacheService.getStats();

        assertNotNull(stats);
        assertTrue(stats.contains("Cache Statistics"));
    }
}
