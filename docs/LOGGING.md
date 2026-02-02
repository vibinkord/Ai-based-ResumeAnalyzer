# Logging & Monitoring Guide

## Overview

This document describes the logging and monitoring capabilities of the AI Resume Analyzer application. The application uses SLF4J with Logback for flexible, structured logging.

## Logging Configuration

### Logback Configuration File

The logging configuration is defined in `src/main/resources/logback.xml`. This file controls:

- **Log Levels**: DEBUG, INFO, WARN, ERROR
- **Appenders**: Console output and rolling file appenders
- **Log Patterns**: Timestamp, thread name, log level, logger name, and message
- **Environment Profiles**: Different logging configurations for dev and prod environments

### Key Configuration Points

#### Log File

- **Location**: `logs/resume-analyzer.log`
- **Rollover Strategy**: Daily or when file size exceeds 10MB
- **Archive Format**: Compressed (`.gz`) with date and index
- **Retention**: Maximum 30 days of archived logs, total 500MB cap

#### Console Output (Development)

- **Format**: Human-readable with colors (depends on terminal support)
- **Useful for**: Local development and debugging

#### File Output (Production)

- **Format**: Detailed with timestamp and thread information
- **Useful for**: Production monitoring and troubleshooting

## Logging Levels

### Package-Specific Log Levels

| Package | Level | Purpose |
|---------|-------|---------|
| `com.resumeanalyzer` | DEBUG | Application code - detailed logging |
| `org.springframework.web` | DEBUG | Spring MVC framework - request/response details |
| `org.springframework` | INFO | General Spring framework - important events only |
| `org.springframework.boot` | INFO | Spring Boot startup - important events only |
| `org.apache.pdfbox` | INFO | PDF processing - errors and important events |
| `org.jsoup` | INFO | HTML parsing - errors and important events |
| `org.apache.http` | WARN | HTTP client - warnings and errors only |

### Root Logger Level

- **Development**: DEBUG (all messages)
- **Production**: INFO (important messages and above)

## Logging in Application Code

### Structured Logging

The application uses structured logging with key information:

```java
log.info("Skill matching completed: {} matched, {} missing, {}% match rate",
        matchResult.getMatchedSkills().size(),
        matchResult.getMissingSkills().size(),
        matchResult.getMatchPercentage());
```

Benefits:
- **Searchable**: Log aggregation tools can search on specific fields
- **Parseable**: Logs can be parsed by monitoring systems
- **Contextual**: Each log message includes relevant data

### Log Messages by Component

#### ResumeAnalysisController

| Level | Message | When |
|-------|---------|------|
| INFO | "Received resume analysis request" | API endpoint called |
| WARN | "Job description resolution failed: {}" | Job description fetch fails |
| ERROR | "Failed to fetch job description from URL" | Network/IO error |
| DEBUG | "Input validation passed, extracting skills" | Validation succeeds |
| DEBUG | "Extracted {} skills from resume and {} skills from job description" | Skills extracted |
| INFO | "Skill matching completed: {} matched, {} missing, {}% match rate" | Matching done |
| DEBUG | "Generated {} rule-based suggestions" | Suggestions created |
| DEBUG | "Generated {} AI-enhanced suggestions" | AI suggestions received |
| DEBUG | "Report generated successfully" | Report created |
| INFO | "Resume analysis completed successfully" | Analysis complete |
| INFO | "File validation passed, proceeding with analysis" | File upload validated |
| WARN | "Resume file is empty or null" | File validation fails |
| ERROR | "Failed to process uploaded resume file: {}" | File processing error |

#### RequestValidator

| Level | Message | When |
|-------|---------|------|
| DEBUG | "Validating analysis request" | Validation starts |
| DEBUG | "Analysis request validation passed" | Both inputs valid |
| DEBUG | "Validating resume text" | Resume validation starts |
| WARN | "Resume text validation failed: text is null or empty" | Resume is empty |
| WARN | "Resume text validation failed: exceeds max size" | Resume too large |
| DEBUG | "Resume text validation passed" | Resume valid |
| DEBUG | "Validating job description text" | Job desc validation starts |
| WARN | "Job description text validation failed: text is null or empty" | Job desc is empty |
| WARN | "Job description text validation failed: exceeds max size" | Job desc too large |
| DEBUG | "Job description text validation passed" | Job desc valid |
| DEBUG | "Validating text content for field: {}" | Content validation starts |
| WARN | "{} text validation failed: contains suspicious patterns" | Injection detected |
| DEBUG | "{} text content validation passed" | Content valid |
| DEBUG | "Detected script injection pattern in text" | Script tag found |
| DEBUG | "Detected null byte in text" | Null byte found |
| DEBUG | "Sanitizing text" | Sanitization starts |
| DEBUG | "Text sanitization completed" | Sanitization done |

#### GlobalExceptionHandler

All exceptions are logged with appropriate levels and error codes. See exception handling documentation for details.

## Log Output Examples

### Development Mode (DEBUG level)

```
2026-02-02 06:35:14.523 [main] DEBUG com.resumeanalyzer.web.controller.ResumeAnalysisController - Received resume analysis request
2026-02-02 06:35:14.524 [main] DEBUG com.resumeanalyzer.validation.RequestValidator - Validating analysis request
2026-02-02 06:35:14.525 [main] DEBUG com.resumeanalyzer.validation.RequestValidator - Validating resume text
2026-02-02 06:35:14.526 [main] DEBUG com.resumeanalyzer.validation.RequestValidator - Validating text content for field: Resume
2026-02-02 06:35:14.527 [main] DEBUG com.resumeanalyzer.validation.RequestValidator - Resume text content validation passed
2026-02-02 06:35:14.528 [main] DEBUG com.resumeanalyzer.validation.RequestValidator - Resume text validation passed
2026-02-02 06:35:14.529 [main] DEBUG com.resumeanalyzer.validation.RequestValidator - Analyzing request validation passed
2026-02-02 06:35:14.530 [main] DEBUG com.resumeanalyzer.web.controller.ResumeAnalysisController - Input validation passed, extracting skills
2026-02-02 06:35:14.545 [main] DEBUG com.resumeanalyzer.web.controller.ResumeAnalysisController - Extracted 12 skills from resume and 15 skills from job description
2026-02-02 06:35:14.550 [main] INFO  com.resumeanalyzer.web.controller.ResumeAnalysisController - Skill matching completed: 10 matched, 5 missing, 66% match rate
2026-02-02 06:35:14.551 [main] DEBUG com.resumeanalyzer.web.controller.ResumeAnalysisController - Generated 5 rule-based suggestions
2026-02-02 06:35:14.600 [main] DEBUG com.resumeanalyzer.web.controller.ResumeAnalysisController - Generated 3 AI-enhanced suggestions
2026-02-02 06:35:14.605 [main] DEBUG com.resumeanalyzer.web.controller.ResumeAnalysisController - Report generated successfully
2026-02-02 06:35:14.606 [main] INFO  com.resumeanalyzer.web.controller.ResumeAnalysisController - Resume analysis completed successfully
```

### Production Mode (INFO level)

```
2026-02-02 06:35:14.523 [main] INFO  com.resumeanalyzer.web.controller.ResumeAnalysisController - Received resume analysis request
2026-02-02 06:35:14.550 [main] INFO  com.resumeanalyzer.web.controller.ResumeAnalysisController - Skill matching completed: 10 matched, 5 missing, 66% match rate
2026-02-02 06:35:14.606 [main] INFO  com.resumeanalyzer.web.controller.ResumeAnalysisController - Resume analysis completed successfully
```

## Environment-Specific Configuration

### Development Profile (dev)

Activate with: `-Dspring.profiles.active=dev`

- Log Level: DEBUG
- Appender: Console only
- Purpose: Detailed debugging with immediate feedback

### Production Profile (prod)

Activate with: `-Dspring.profiles.active=prod`

- Log Level: INFO
- Appender: File only
- Purpose: Minimal performance impact with archival

## Best Practices

### When Adding Logging

1. **Use appropriate log levels**:
   - DEBUG: Detailed information for developers
   - INFO: Important business events and milestones
   - WARN: Potential problems that should be investigated
   - ERROR: Error conditions that need attention

2. **Include context**:
   ```java
   log.info("Processing resumed with ID: {} and status: {}", resumeId, status);
   ```

3. **Don't log sensitive information**:
   - Passwords, tokens, API keys
   - Personal identification numbers
   - Credit card numbers

4. **Use lazy argument evaluation**:
   ```java
   // Good - {} placeholders avoid string concatenation if DEBUG is disabled
   log.debug("Extracted {} skills", skillCount);
   
   // Avoid - string concatenation happens even if DEBUG is disabled
   // log.debug("Extracted " + skillCount + " skills");
   ```

5. **Log at method entry and exit for complex operations**:
   ```java
   log.debug("Validating analysis request");
   try {
       // implementation
       log.debug("Analysis request validation passed");
   } catch (Exception e) {
       log.error("Analysis request validation failed", e);
       throw e;
   }
   ```

## Monitoring and Alerting

### Recommended Monitoring Points

1. **Analysis Success Rate**: Monitor INFO level logs for "Resume analysis completed successfully"
2. **Validation Failures**: Monitor WARN level logs for validation failures
3. **File Processing Errors**: Monitor ERROR level logs for file processing failures
4. **API Response Times**: Add timestamp difference calculations in logs

### Log Analysis Tools

Consider integrating with:
- **ELK Stack** (Elasticsearch, Logstash, Kibana): Full-featured log aggregation
- **Splunk**: Enterprise-grade log management
- **CloudWatch**: AWS-native log monitoring
- **Datadog**: Cloud-based monitoring and analytics

## Performance Considerations

1. **File Rollover**: Configured at 10MB with daily rollover to prevent disk space issues
2. **Async Appender**: Available (commented out) for high-throughput scenarios
3. **Log Levels**: Set appropriately to avoid excessive I/O in production

## Troubleshooting

### Logs not appearing

1. Check that logback.xml is in `src/main/resources/`
2. Verify log level configuration matches your needs
3. Check file permissions for log directory
4. Ensure SLF4J dependencies are correctly configured

### Logs are too verbose

1. Increase log level thresholds in logback.xml
2. Reduce logging in specific packages
3. Use production profile to limit to INFO and above

### Disk space issues

1. Monitor log file size and rollover configuration
2. Archive old logs to external storage
3. Implement log rotation policies

## Configuration Reference

### File Appender Properties

| Property | Value | Purpose |
|----------|-------|---------|
| `maxFileSize` | 10MB | Size threshold for file rollover |
| `maxHistory` | 30 | Days of archived logs to keep |
| `totalSizeCap` | 500MB | Maximum total size of all log files |

### Logger Packages

| Package | Level | Notes |
|---------|-------|-------|
| `com.resumeanalyzer` | DEBUG | Application code |
| `org.springframework.web` | DEBUG | Spring MVC details |
| `org.springframework` | INFO | Spring framework events |
| `org.apache.pdfbox` | INFO | PDF processing |
| `org.jsoup` | INFO | HTML parsing |
| `org.apache.http` | WARN | HTTP client warnings |

## Future Enhancements

1. **Metrics Integration**: Add Micrometer for performance metrics
2. **Distributed Tracing**: Implement OpenTelemetry for multi-service tracing
3. **Custom Log Correlation**: Add request IDs for tracing across components
4. **Async Appenders**: Enable for high-throughput production deployments
