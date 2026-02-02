# Swagger/OpenAPI Documentation

## Table of Contents
1. [Overview](#overview)
2. [Accessing Swagger UI](#accessing-swagger-ui)
3. [API Endpoints](#api-endpoints)
4. [Data Models](#data-models)
5. [Configuration](#configuration)
6. [Usage Examples](#usage-examples)
7. [Features & Benefits](#features--benefits)

---

## Overview

The AI Resume Analyzer API includes comprehensive Swagger/OpenAPI 3.0 documentation that is automatically generated from the codebase. This documentation provides:

- **Interactive API Explorer**: Test all endpoints directly from the browser
- **Auto-generated Documentation**: Updated automatically as code changes
- **Request/Response Examples**: See exactly what to send and expect
- **Data Model Definitions**: Clear descriptions of all request and response objects
- **Standard Compliance**: Full OpenAPI 3.0 specification compliance

### Technology Stack

- **Framework**: SpringDoc OpenAPI Starter (v2.0.4)
- **Swagger UI**: v4.18.1
- **OpenAPI Specification**: v3.0.0
- **Java Annotations**: `@Operation`, `@ApiResponse`, `@Schema`, `@Tag`

---

## Accessing Swagger UI

### Development Environment

The Swagger UI is available at the following URLs when the application is running:

```
http://localhost:8084/swagger-ui.html
```

### Accessing OpenAPI Specification

The raw OpenAPI specification can be accessed in multiple formats:

**JSON Format:**
```
http://localhost:8084/v3/api-docs
```

**YAML Format:**
```
http://localhost:8084/v3/api-docs.yaml
```

### Customized API Documentation

The API documentation can be accessed for specific controller tags:

```
http://localhost:8084/v3/api-docs?groups=Resume%20Analysis
http://localhost:8084/v3/api-docs?groups=Utility
```

---

## API Endpoints

### Resume Analysis Endpoints

#### 1. Analyze Resume Text

**Endpoint**: `POST /api/analyze`

**Description**: Analyzes a resume and matches it against a job description using both rule-based and AI-enhanced suggestions.

**Request Body**:
```json
{
  "resumeText": "Senior Java Developer with 5 years experience in Spring Boot, microservices, and cloud platforms...",
  "jobDescriptionText": "We are looking for a Java Developer with Spring Boot and AWS experience..."
}
```

**Response** (200 OK):
```json
{
  "matchPercentage": 75.5,
  "matchedSkills": ["Java", "Spring Boot", "Microservices"],
  "missingSkills": ["AWS", "Docker"],
  "suggestions": [
    "Consider adding AWS to your skillset",
    "Highlight more cloud deployment experience"
  ],
  "aiSuggestions": [
    "Consider AWS certification to boost your profile",
    "Add container orchestration experience"
  ],
  "report": "Detailed analysis report..."
}
```

**Error Response** (400 Bad Request):
```json
{
  "error": "Resume text is required and must be at least 50 characters",
  "code": "VALIDATION_ERROR",
  "timestamp": "2024-01-15T10:30:45.123456",
  "details": "Resume text length: 45. Minimum required: 50",
  "path": "/api/analyze"
}
```

#### 2. Analyze Resume File

**Endpoint**: `POST /api/analyze-file`

**Description**: Analyzes an uploaded resume file (PDF, DOCX, or TXT) against a job description.

**Request Parameters**:
- `resumeFile` (file, required): The resume file to analyze (PDF, DOCX, or TXT)
- `jobDescriptionText` (string, optional): Job description text
- `jobDescriptionUrl` (string, optional): URL to fetch job description from

**Example cURL Command**:
```bash
curl -X POST "http://localhost:8084/api/analyze-file" \
  -F "resumeFile=@resume.pdf" \
  -F "jobDescriptionText=Senior Developer needed with Java and Spring Boot experience..."
```

**Response**: Same as `/api/analyze` endpoint

---

### Utility Endpoints

#### 3. Health Check

**Endpoint**: `GET /api/v1/health`

**Description**: Returns the health status of the API.

**Response** (200 OK):
```json
{
  "status": "UP",
  "message": "AI Resume Analyzer is running",
  "version": "1.0.0",
  "timestamp": "2024-01-15T10:30:45.123456"
}
```

#### 4. Get Known Skills

**Endpoint**: `GET /api/v1/skills`

**Description**: Returns a comprehensive list of 115+ technical skills recognized by the analyzer.

**Response** (200 OK):
```json
{
  "skills": [
    "Java",
    "Python",
    "Spring Boot",
    "AWS",
    "Docker",
    "Kubernetes",
    "PostgreSQL",
    "MongoDB",
    "React",
    "Angular",
    "...(115+ total)"
  ],
  "totalCount": 115,
  "description": "All recognized technical skills in the system"
}
```

#### 5. Batch Analyze Resumes

**Endpoint**: `POST /api/v1/batch`

**Description**: Analyzes multiple resumes against job descriptions in a single request.

**Request Body**:
```json
{
  "items": [
    {
      "id": "item-1",
      "resumeText": "Senior Java Developer with 5 years experience...",
      "jobDescriptionText": "We need a Java developer..."
    },
    {
      "id": "item-2",
      "resumeText": "Python expert with 3 years ML experience...",
      "jobDescriptionText": "Looking for ML engineer with Python..."
    }
  ]
}
```

**Response** (200 OK):
```json
{
  "results": [
    {
      "id": "item-1",
      "success": true,
      "message": "Analysis completed",
      "data": {
        "matchPercentage": 75.5,
        "matchedSkills": ["Java", "Spring Boot"],
        "missingSkills": ["AWS"],
        "suggestions": [],
        "aiSuggestions": [],
        "report": "..."
      }
    },
    {
      "id": "item-2",
      "success": true,
      "message": "Analysis completed",
      "data": { /* ... */ }
    }
  ],
  "successCount": 2,
  "failureCount": 0,
  "timestamp": "2024-01-15T10:30:45.123456"
}
```

#### 6. Compare Resumes

**Endpoint**: `POST /api/v1/compare`

**Description**: Compares two resumes to identify common and unique skills.

**Request Body**:
```json
{
  "resume1": "Senior Java Developer with 5 years experience in Spring Boot...",
  "resume2": "Mid-level Python Developer with 3 years experience in Django..."
}
```

**Response** (200 OK):
```json
{
  "resume1SkillCount": 25,
  "resume2SkillCount": 20,
  "commonSkills": ["Docker", "Git", "SQL"],
  "uniqueToResume1": ["Java", "Spring Boot", "AWS"],
  "uniqueToResume2": ["Python", "Django", "TensorFlow"],
  "similarityPercentage": 45.5,
  "timestamp": "2024-01-15T10:30:45.123456"
}
```

---

## Data Models

### ResumeAnalysisRequest

| Field | Type | Required | Description | Example |
|-------|------|----------|-------------|---------|
| `resumeText` | string | Yes | Resume content (50-50000 chars) | "Senior Java Developer..." |
| `jobDescriptionText` | string | Conditional* | Job description text | "Looking for Java dev..." |
| `jobDescriptionUrl` | string | Conditional* | URL to fetch job description | "https://example.com/job/123" |

*One of `jobDescriptionText` or `jobDescriptionUrl` is required.

### ResumeAnalysisResponse

| Field | Type | Description |
|-------|------|-------------|
| `matchPercentage` | double | Overall match score (0-100) |
| `matchedSkills` | Set<string> | Skills found in both resume and job |
| `missingSkills` | Set<string> | Required skills missing from resume |
| `suggestions` | List<string> | Rule-based improvement suggestions |
| `aiSuggestions` | List<string> | AI-enhanced suggestions from Gemini |
| `report` | string | Formatted text report |

### HealthResponse

| Field | Type | Description |
|-------|------|-------------|
| `status` | string | Health status ("UP" or "DOWN") |
| `message` | string | Status message |
| `version` | string | API version |
| `timestamp` | LocalDateTime | Response timestamp |

### SkillListResponse

| Field | Type | Description |
|-------|------|-------------|
| `skills` | List<string> | List of known skills |
| `totalCount` | int | Total number of skills |
| `description` | string | Description of skill list |

### ComparisonRequest

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `resume1` | string | Yes | First resume (50-50000 chars) |
| `resume2` | string | Yes | Second resume (50-50000 chars) |

### ComparisonResponse

| Field | Type | Description |
|-------|------|-------------|
| `resume1SkillCount` | int | Total unique skills in resume 1 |
| `resume2SkillCount` | int | Total unique skills in resume 2 |
| `commonSkills` | List<string> | Skills present in both |
| `uniqueToResume1` | List<string> | Skills only in resume 1 |
| `uniqueToResume2` | List<string> | Skills only in resume 2 |
| `similarityPercentage` | double | Similarity score (0-100) |
| `timestamp` | LocalDateTime | Comparison timestamp |

### ErrorResponse

| Field | Type | Description |
|-------|------|-------------|
| `error` | string | Error message |
| `code` | string | Error code (VALIDATION_ERROR, FILE_PROCESSING_ERROR, etc.) |
| `timestamp` | LocalDateTime | When error occurred |
| `details` | string | Additional error details (optional) |
| `path` | string | Request path (optional) |

---

## Configuration

### Swagger Configuration File

The Swagger configuration is defined in:
```
src/main/java/com/resumeanalyzer/config/SwaggerConfig.java
```

Key configuration aspects:

```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Resume Analyzer API")
                        .version("1.0.0")
                        .description("Comprehensive resume analysis with skill matching...")
                        .contact(new Contact()...)
                        .license(new License()...))
                .addServersItem(new Server()
                        .url("http://localhost:8084")
                        .description("Development Server"))
                .addServersItem(new Server()
                        .url("https://api.resumeanalyzer.com")
                        .description("Production Server"));
    }
}
```

### Controller Annotations

All controllers are annotated with `@Tag` to organize endpoints:

```java
@RestController
@RequestMapping("/api")
@Tag(name = "Resume Analysis", description = "Endpoints for analyzing resumes...")
public class ResumeAnalysisController { ... }

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Utility", description = "Utility endpoints for health checks...")
public class UtilityController { ... }
```

### Endpoint Annotations

All endpoint methods are documented with:

```java
@PostMapping("/analyze")
@Operation(
    summary = "Analyze resume text against job description",
    description = "Analyzes a resume...",
    operationId = "analyzeResume"
)
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "200",
        description = "Resume analysis completed successfully",
        content = @Content(schema = @Schema(implementation = ResumeAnalysisResponse.class))
    ),
    @ApiResponse(responseCode = "400", description = "Invalid input"),
    @ApiResponse(responseCode = "500", description = "Server error")
})
public ResponseEntity<ResumeAnalysisResponse> analyze(@RequestBody ResumeAnalysisRequest request) {
    // Implementation...
}
```

---

## Usage Examples

### Example 1: Text-Based Resume Analysis (cURL)

```bash
curl -X POST "http://localhost:8084/api/analyze" \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "Senior Java Developer with 5 years experience in Spring Boot, microservices architecture, and AWS cloud platforms. Proficient in Docker, Kubernetes, and CI/CD pipelines.",
    "jobDescriptionText": "We are hiring a Java Developer with Spring Boot experience. Required skills: Java, Spring Boot, Docker, AWS, and SQL databases."
  }'
```

### Example 2: File Upload with Resume (cURL)

```bash
curl -X POST "http://localhost:8084/api/analyze-file" \
  -F "resumeFile=@/path/to/resume.pdf" \
  -F "jobDescriptionText=Senior Java Developer needed with 5+ years experience..."
```

### Example 3: Health Check (Python)

```python
import requests

response = requests.get('http://localhost:8084/api/v1/health')
health_data = response.json()
print(f"Status: {health_data['status']}")
print(f"Version: {health_data['version']}")
```

### Example 4: Get All Known Skills (JavaScript)

```javascript
fetch('http://localhost:8084/api/v1/skills')
  .then(response => response.json())
  .then(data => {
    console.log(`Total Skills: ${data.totalCount}`);
    console.log(data.skills);
  });
```

### Example 5: Compare Two Resumes (Python)

```python
import requests
import json

url = 'http://localhost:8084/api/v1/compare'
data = {
    'resume1': 'Senior Java Developer...',
    'resume2': 'Mid-level Python Developer...'
}

response = requests.post(url, json=data)
comparison = response.json()
print(f"Similarity: {comparison['similarityPercentage']}%")
print(f"Common Skills: {comparison['commonSkills']}")
```

### Example 6: Batch Analysis (JavaScript)

```javascript
const items = [
  {
    id: 'batch-1',
    resumeText: 'Senior Java Developer...',
    jobDescriptionText: 'Looking for Java expert...'
  },
  {
    id: 'batch-2',
    resumeText: 'Python ML Engineer...',
    jobDescriptionText: 'Need ML engineer with Python...'
  }
];

fetch('http://localhost:8084/api/v1/batch', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ items })
})
.then(r => r.json())
.then(result => {
  console.log(`Success: ${result.successCount}, Failed: ${result.failureCount}`);
});
```

---

## Features & Benefits

### Interactive Testing

- **Try It Out**: Test all endpoints directly in Swagger UI
- **Real Responses**: See actual API responses with your test data
- **Error Scenarios**: Test different error conditions and see error responses
- **Live Examples**: Pre-filled example requests for quick testing

### Documentation

- **Auto-Updated**: Documentation stays in sync with code
- **Comprehensive Descriptions**: Every field and parameter is explained
- **Examples**: Request/response examples for all endpoints
- **Data Types**: Clear specification of accepted data types and formats

### Standards Compliance

- **OpenAPI 3.0**: Full OpenAPI 3.0 specification compliance
- **REST Best Practices**: Proper HTTP methods, status codes, and headers
- **JSON Schema**: Complete JSON schema validation
- **HATEOAS Ready**: Built for hypermedia-driven APIs

### Developer Experience

- **Zero Setup**: Works out of the box, no manual documentation
- **Schema Validation**: Automatic validation of requests against schema
- **Code Generation**: Can be used to generate client libraries
- **Integration Testing**: Use OpenAPI spec for automated testing

### Security

- **No Sensitive Data**: API keys not exposed in documentation
- **Error Masking**: Detailed errors in development, generic in production
- **CORS Ready**: Pre-configured for cross-origin requests
- **Rate Limiting**: Built-in support for rate limiting

---

## Accessing Different Environments

### Development

```
Swagger UI: http://localhost:8084/swagger-ui.html
API Docs: http://localhost:8084/v3/api-docs
```

### Production

Update the server URL in `SwaggerConfig.java`:

```java
.addServersItem(new Server()
        .url("https://api.yourdomain.com")
        .description("Production Server"))
```

---

## Troubleshooting

### Swagger UI Not Loading

**Problem**: 404 error when accessing `/swagger-ui.html`

**Solution**: 
- Ensure `springdoc-openapi-starter-webmvc-ui` dependency is in `pom.xml`
- Clear Maven cache: `mvn clean install`
- Restart the application

### Missing Endpoint Documentation

**Problem**: Some endpoints not showing in Swagger UI

**Solution**:
- Check if controller has `@Tag` annotation
- Verify methods have `@Operation` annotation
- Rebuild project: `mvn clean package`

### Schema Not Displaying Correctly

**Problem**: Data model schemas appear empty

**Solution**:
- Verify DTOs have `@Schema` annotation on class
- Check all fields have `@Schema` annotations with descriptions
- Ensure correct imports from `io.swagger.v3.oas.annotations.media`

---

## Advanced Features

### Custom Server Variables

Configure environment-specific servers in `SwaggerConfig.java`:

```java
.addServersItem(new Server()
        .url("https://{environment}.resumeanalyzer.com")
        .description("Environment-specific server"))
```

### Security Schemes

Add API key security (future enhancement):

```java
.components(new Components()
        .addSecuritySchemes("api_key", new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-API-Key")))
```

### Grouping Endpoints

Tag-based grouping is already implemented:
- **Resume Analysis** tag: `/api/*` endpoints
- **Utility** tag: `/api/v1/*` endpoints

---

## Testing with Swagger UI

1. Open `http://localhost:8084/swagger-ui.html`
2. Select an endpoint from the list
3. Click "Try it out"
4. Fill in request parameters/body
5. Click "Execute"
6. View response, headers, and curl command

---

## Integration with CI/CD

The OpenAPI specification can be used in CI/CD pipelines:

```bash
# Download OpenAPI spec
curl http://localhost:8084/v3/api-docs > openapi.json

# Validate spec
swagger-cli validate openapi.json

# Generate client
openapi-generator-cli generate -i openapi.json -g python-client -o ./client
```

---

## References

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.3)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
- [Spring Boot Integration](https://springdoc.org/v1.6.14/properties.html)

---

**Last Updated**: January 15, 2024  
**Version**: 1.0.0  
**Status**: Production Ready ✅
