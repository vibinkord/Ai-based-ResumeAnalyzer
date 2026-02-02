# Configuration Management Guide

## Overview

The AI Resume Analyzer uses Spring Boot's configuration system to manage application settings across different environments (development, test, production). All configuration is externalized and can be easily customized without changing code.

## Quick Start

### Development
```bash
# Use default configuration (dev profile)
mvn spring-boot:run

# Or explicitly set dev profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Production
```bash
# Set environment variables
export SPRING_PROFILES_ACTIVE=prod
export GEMINI_API_KEY=your-actual-api-key
export CORS_ALLOWED_ORIGINS=https://yourdomain.com

# Run the application
java -jar resume-analyzer.jar
```

## Configuration Files

### Main Configuration File
**File**: `src/main/resources/application.properties`

Contains all configurable properties with default values. This file is the base configuration used in all profiles.

### Profile-Specific Configurations

#### Development Profile
**File**: `src/main/resources/application-dev.properties`

Activated with: `spring.profiles.active=dev`

**Characteristics**:
- Debug logging enabled
- Relaxed validation (100MB file uploads allowed)
- All API features enabled
- Swagger UI accessible at `/swagger-ui.html`
- CORS allows all origins
- Console logging output

**When to Use**: Local development, debugging, testing new features

#### Test Profile
**File**: `src/main/resources/application-test.properties`

Activated with: `spring.profiles.active=test`

**Characteristics**:
- Minimal logging (WARN level)
- In-memory H2 database
- AI suggestions disabled (use mocks)
- Fast execution
- All features enabled for testing

**When to Use**: Running unit and integration tests

#### Production Profile
**File**: `src/main/resources/application-prod.properties`

Activated with: `spring.profiles.active=prod` (or `SPRING_PROFILES_ACTIVE=prod`)

**Characteristics**:
- INFO level logging only
- File-based logging with 90-day retention
- Strict validation (50MB file uploads)
- Swagger UI disabled for security
- CORS restricted to specific domains
- Graceful shutdown enabled
- Performance optimizations enabled

**When to Use**: Production deployments

## Configuration Categories

### 1. Server Configuration

```properties
# Port and context path
server.port=8084
server.servlet.context-path=/

# Shutdown behavior
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
```

**Environment Variables**:
- `SERVER_PORT`: Override server port

### 2. Logging Configuration

```properties
# Log levels
logging.level.root=INFO
logging.level.com.resumeanalyzer=DEBUG
logging.level.org.springframework=INFO

# File logging
logging.file.name=logs/resume-analyzer.log
logging.file.max-size=10MB
logging.file.max-history=30
```

**Environment Variables**:
- `LOG_LEVEL`: Override root log level

### 3. Resume Analysis Configuration

```properties
# File size limits
resume.analyzer.max-resume-size=51200
resume.analyzer.max-job-description-size=51200
resume.analyzer.min-text-length=1

# Skill extraction
resume.analyzer.skills.enabled=true
resume.analyzer.skills.config-file=classpath:skills.json
resume.analyzer.skills.cache-enabled=true

# Match thresholds
resume.analyzer.match.good-match-threshold=70
resume.analyzer.match.acceptable-match-threshold=50
resume.analyzer.match.poor-match-threshold=30
```

**Usage in Code**:
```java
@Autowired
private ResumeAnalyzerProperties resumeAnalyzerProperties;

public void analyze() {
    int maxSize = resumeAnalyzerProperties.getMaxResumeSize();
    // Use the configuration
}
```

### 4. Gemini API Configuration

```properties
# API credentials and endpoint
gemini.api.key=${GEMINI_API_KEY:default-key}
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent

# Model parameters
gemini.model.name=gemini-flash-latest
gemini.model.temperature=0.7
gemini.model.max-output-tokens=500
gemini.model.top-p=0.9
gemini.model.top-k=40
```

**Environment Variables** (REQUIRED in Production):
- `GEMINI_API_KEY`: Your Google Gemini API key (get from https://ai.google.dev/)

**Note**: Always use environment variables for secrets in production!

### 5. Security Configuration

```properties
# Input validation
security.input-validation.enabled=true
security.sanitize-input=true
security.block-sql-injection=true
security.block-script-injection=true
security.block-null-bytes=true

# CORS configuration
spring.web.cors.allowed-origins=http://localhost:3000
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE
spring.web.cors.max-age=3600
```

**Environment Variables**:
- `CORS_ALLOWED_ORIGINS`: Comma-separated list of allowed origins

### 6. File Upload Configuration

```properties
# Multipart upload limits
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# Supported file types
file.upload.allowed-types=.pdf,.txt,.docx
file.upload.check-mime-type=true
```

### 7. Batch Processing Configuration

```properties
batch.processing.enabled=true
batch.processing.max-items=100
batch.processing.timeout-seconds=300
batch.processing.thread-pool-size=5
```

### 8. Feature Flags

```properties
# Enable/disable features
features.health-check.enabled=true
features.skills-list.enabled=true
features.batch-analysis.enabled=true
features.resume-comparison.enabled=true
features.file-upload.enabled=true
features.ai-suggestions.enabled=true
```

**Usage in Code**:
```java
@Autowired
private FeaturesProperties featuresProperties;

if (featuresProperties.getAiSuggestions().isEnabled()) {
    // Generate AI suggestions
}
```

### 9. Performance & Caching

```properties
# Cache configuration
spring.cache.type=simple
spring.cache.cache-names=skills,resume-cache

# Database connection pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

### 10. API Documentation (Swagger)

```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

**Access Swagger UI**: `http://localhost:8084/swagger-ui.html` (dev/test only)

## Environment-Specific Behavior

| Setting | Development | Test | Production |
|---------|-------------|------|------------|
| Server Port | 8084 | Random | 8080 |
| Log Level | DEBUG | WARN | INFO |
| Log Output | Console | Console | File |
| Max Upload | 100MB | 50MB | 50MB |
| Swagger UI | Enabled | Disabled | Disabled |
| CORS Origins | * | * | Restricted |
| AI Suggestions | Enabled | Disabled | Enabled |
| Cache | Simple | None | Caffeine |

## Using Environment Variables

### In Docker
```dockerfile
ENV SPRING_PROFILES_ACTIVE=prod
ENV GEMINI_API_KEY=your-api-key
ENV CORS_ALLOWED_ORIGINS=https://yourdomain.com
ENV SERVER_PORT=8080
```

### In Kubernetes
```yaml
env:
  - name: SPRING_PROFILES_ACTIVE
    value: prod
  - name: GEMINI_API_KEY
    valueFrom:
      secretKeyRef:
        name: gemini-secret
        key: api-key
```

### In System Environment
```bash
export SPRING_PROFILES_ACTIVE=prod
export GEMINI_API_KEY=your-api-key
export CORS_ALLOWED_ORIGINS=https://yourdomain.com
java -jar resume-analyzer.jar
```

## Configuration Properties Classes

The application uses `@ConfigurationProperties` classes to map properties:

### ResumeAnalyzerProperties
Maps `resume.analyzer.*` properties
- Skills configuration
- Match thresholds
- AI suggestion settings

### GeminiProperties
Maps `gemini.*` properties
- API credentials and URL
- Model parameters

### SecurityProperties
Maps `security.*` properties
- Input validation settings
- Injection attack prevention

### FeaturesProperties
Maps `features.*` properties
- Feature flag toggles

**Usage Example**:
```java
@Autowired
private ResumeAnalyzerProperties analyzerProps;
@Autowired
private GeminiProperties geminiProps;
@Autowired
private SecurityProperties securityProps;
@Autowired
private FeaturesProperties featuresProps;

public void initialize() {
    if (analyzerProps.getSkills().isEnabled()) {
        // Initialize skills
    }
    
    if (geminiProps.getApi().getKey() != null) {
        // Configure Gemini API
    }
    
    if (securityProps.getInputValidation().isEnabled()) {
        // Enable validation
    }
}
```

## Common Configuration Scenarios

### Scenario 1: Increase File Upload Limit
```properties
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
resume.analyzer.max-resume-size=102400
```

### Scenario 2: Disable AI Suggestions
```properties
resume.analyzer.ai-suggestions.enabled=false
features.ai-suggestions.enabled=false
```

### Scenario 3: Enable Batch Processing
```properties
batch.processing.enabled=true
batch.processing.max-items=50
features.batch-analysis.enabled=true
```

### Scenario 4: Change Log Level for Specific Component
```properties
logging.level.com.resumeanalyzer.analysis=TRACE
logging.level.com.resumeanalyzer.web.controller=DEBUG
```

### Scenario 5: Production-Ready Setup
```bash
export SPRING_PROFILES_ACTIVE=prod
export GEMINI_API_KEY=$(aws secretsmanager get-secret-value --secret-id gemini-api-key)
export CORS_ALLOWED_ORIGINS=https://mycompany.com
export LOG_LEVEL=INFO
java -jar resume-analyzer.jar
```

## Validation & Error Handling

The application validates configuration at startup:
- Missing required properties (GEMINI_API_KEY in prod) → Application fails to start
- Invalid property values → Warning logged, defaults used
- Conflicting settings → Warning logged, one takes precedence

## Best Practices

1. **Never hardcode secrets** - Use environment variables for API keys
2. **Use profiles** - Keep dev/test/prod configurations separate
3. **Document changes** - Update this file when adding new configurations
4. **Validate on startup** - Use `@Validated` on properties classes
5. **Use meaningful defaults** - Provide sensible defaults for optional properties
6. **Environment-specific** - Override only what's different per environment
7. **Monitor logs** - Set appropriate log levels per environment
8. **Cache wisely** - Enable caching in production, disable in development

## Configuration Validation

To validate configurations:

```java
@Autowired
private ResumeAnalyzerProperties props;

@PostConstruct
public void validate() {
    assert props.getMaxResumeSize() > 0 : "Max resume size must be positive";
    assert props.getSkills().getConfigFile() != null : "Skills config file required";
}
```

## Troubleshooting

### Issue: Configuration not applied
**Solution**: Check if property name is correct and file is in classpath

### Issue: Wrong profile being used
**Solution**: Check `SPRING_PROFILES_ACTIVE` environment variable

### Issue: API key not recognized
**Solution**: Verify `GEMINI_API_KEY` environment variable is set correctly

### Issue: Port already in use
**Solution**: Change `server.port` or set `SERVER_PORT` environment variable

## Additional Resources

- [Spring Boot Configuration Documentation](https://spring.io/guides/gs/centralized-configuration/)
- [Spring Boot Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
- [12-Factor App Configuration](https://12factor.net/config)
