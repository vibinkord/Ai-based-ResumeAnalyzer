# AI Resume Analyzer - Web Stage W1

## Spring Boot REST API

### Overview
This stage adds a Spring Boot REST layer exposing the existing console analysis logic via HTTP.

### Technology Stack
- Spring Boot 3.2.1
- Spring Web (REST)
- Java 17
- Maven

### Build and Run

#### Using Maven
```bash
mvn clean install
mvn spring-boot:run
```

#### Using Java
```bash
mvn clean package
java -jar target/resume-analyzer-1.0.0.jar
```

### API Endpoints

#### POST /api/analyze
Analyzes a resume against a job description.

**Request:**
```json
{
  "resumeText": "John Doe\nJava Developer\nSkills: Java, OOP, SQL, REST",
  "jobDescriptionText": "Looking for Java developer with Spring Boot and SQL experience"
}
```

**Response:**
```json
{
  "matchPercentage": 66.7,
  "matchedSkills": ["Java", "OOP", "SQL", "REST"],
  "missingSkills": ["Spring"],
  "suggestions": [
    "Add hands-on experience with Spring to your resume.",
    "Improve project descriptions to better highlight relevant skills mentioned in the job posting.",
    "Optimize resume keywords to match ATS scanning patterns used by recruiters."
  ],
  "report": "==============================\nAI Resume Analysis Report\n==============================\n..."
}
```

### Testing with cURL

```bash
curl -X POST http://localhost:8080/api/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "John Doe\nEmail: john.doe@example.com\nSkills: Java, SQL, Git, OOP, REST, JSON",
    "jobDescriptionText": "Senior Java Engineer\nRequired: Java, SQL, Spring Boot, REST, OOP"
  }'
```

### Testing with Postman

1. Method: POST
2. URL: `http://localhost:8080/api/analyze`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "resumeText": "John Doe\nJava Developer with 5 years experience\nSkills: Java, OOP, SQL, REST, Git",
  "jobDescriptionText": "We need a Java developer with Spring Boot, SQL, and REST experience"
}
```

### Application Startup Verification

After starting the application, you should see:
```
Started ResumeAnalyzerApplication in X.XXX seconds
```

The API will be available at: `http://localhost:8080/api/analyze`

### Architecture

```
POST /api/analyze
    ↓
ResumeAnalysisController
    ↓
[Existing Domain Logic - No Changes]
    ├── SkillExtractor (Stage 2)
    ├── SkillMatcher (Stage 3)
    ├── ResumeSuggestionEngine (Stage 4)
    └── ResumeReportGenerator (Stage 5)
    ↓
AnalyzeResponse (DTO)
    ↓
JSON Response
```

### Project Structure
```
src/main/java/com/resumeanalyzer/
├── analysis/          [Existing - Stage 2 & 3]
├── suggestions/       [Existing - Stage 4]
├── report/           [Existing - Stage 5]
├── io/               [Existing - Stage 1]
├── util/             [Existing - Stage 1]
└── web/              [NEW - Web Stage W1]
    ├── ResumeAnalyzerApplication.java
    ├── controller/
    │   └── ResumeAnalysisController.java
    └── dto/
        ├── AnalyzeRequest.java
        └── AnalyzeResponse.java
```

### Status
**Web Stage W1 complete | Web stages remaining: 3**

### Next Stages (Future)
- W2: File upload support (PDF/TXT)
- W3: Frontend UI (React/Thymeleaf)
- W4: AI API integration (OpenAI/Azure)
