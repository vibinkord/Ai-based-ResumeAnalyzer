package com.resumeanalyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Elasticsearch Configuration Properties
 * 
 * Configuration values for Elasticsearch connection and behavior:
 * 
 * Properties (application.properties):
 * - elasticsearch.host: Elasticsearch host (default: localhost)
 * - elasticsearch.port: Elasticsearch port (default: 9200)
 * - elasticsearch.connect-timeout: Connection timeout in ms (default: 5000)
 * - elasticsearch.socket-timeout: Socket timeout in ms (default: 60000)
 * - elasticsearch.username: Authentication username (optional)
 * - elasticsearch.password: Authentication password (optional)
 * - elasticsearch.enabled: Enable Elasticsearch support (default: false)
 * 
 * @author Resume Analyzer Team
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {
    
    /**
     * Elasticsearch host address
     */
    private String host = "localhost";
    
    /**
     * Elasticsearch port number
     */
    private int port = 9200;
    
    /**
     * Connection timeout in milliseconds
     */
    private int connectTimeout = 5000;
    
    /**
     * Socket timeout in milliseconds
     */
    private int socketTimeout = 60000;
    
    /**
     * Authentication username
     */
    private String username = "";
    
    /**
     * Authentication password
     */
    private String password = "";
    
    /**
     * Enable Elasticsearch support
     */
    private boolean enabled = false;
}
