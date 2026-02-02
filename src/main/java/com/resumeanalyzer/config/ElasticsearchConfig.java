package com.resumeanalyzer.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch Configuration for Full-Text Search
 * 
 * Configures Elasticsearch support with:
 * - Repository scanning
 * - Configuration properties loading
 * - Optional activation based on properties
 * 
 * Elasticsearch Usage:
 * - Full-text search on resume content
 * - Fast query execution for analytics
 * - Aggregation support for metrics
 * - Real-time indexing capabilities
 * 
 * To enable Elasticsearch, set:
 * elasticsearch.enabled=true in application.properties
 * 
 * @author Resume Analyzer Team
 * @version 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "true", matchIfMissing = false)
@EnableElasticsearchRepositories(basePackages = "com.resumeanalyzer.repository.search")
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchConfig {
}
