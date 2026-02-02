package com.resumeanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application Entry Point
 * 
 * This class bootstraps the Spring Boot application and starts
 * the embedded Tomcat server on port 8080 (default).
 * 
 * @SpringBootApplication enables:
 * - @Configuration: Spring Java config
 * - @EnableAutoConfiguration: Auto-configuration based on classpath
 * - @ComponentScan: Scans for Spring components in this package and sub-packages
 */
@SpringBootApplication
public class ResumeAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResumeAnalyzerApplication.class, args);
    }
}
