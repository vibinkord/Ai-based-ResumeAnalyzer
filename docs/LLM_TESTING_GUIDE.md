# LLM Integration Testing Guide

## Quick Start Testing

### 1. Start the Application
```bash
cd "d:\College Project\Resume analyser"
mvn spring-boot:run
```

Server starts on: `http://localhost:8081`

### 2. Test Endpoint: /api/analyze

#### With cURL (No API Key)
```bash
curl -X POST http://localhost:8081/api/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "Java Developer with 3 years of Spring Boot experience.",
    "jobDescriptionText": "Senior Backend Engineer needed with 5 years Java, Spring Boot, Docker, and AWS."
  }'
```

**Expected Response (without API key):**
```json
{
  "matchPercentage": 40.0,
  "matchedSkills": ["Java", "Spring Boot"],
  "missingSkills": ["Docker", "AWS"],
  "suggestions": [
    "Add hands-on experience with Docker to your resume.",
    "Add hands-on experience with AWS to your resume.",
    "Your resume needs major alignment with job requirements. Restructure to highlight required skills more prominently.",
    "Consider adding a 'Core Competencies' section to emphasize missing technical areas."
  ],
  "aiSuggestions": [],
  "report": "[Detailed Analysis Report]"
}
```

#### With cURL (With API Key)
First, set environment variable:
```bash
# Windows
set GEMINI_API_KEY=your-api-key

# PowerShell
$env:GEMINI_API_KEY="your-api-key"

# Linux/Mac
export GEMINI_API_KEY="your-api-key"
```

Then run the same cURL command. **Expected Response (with API key):**
```json
{
  "matchPercentage": 40.0,
  "matchedSkills": ["Java", "Spring Boot"],
  "missingSkills": ["Docker", "AWS"],
  "suggestions": [
    "Add hands-on experience with Docker to your resume.",
    "Add hands-on experience with AWS to your resume.",
    "Your resume needs major alignment with job requirements. Restructure to highlight required skills more prominently.",
    "Consider adding a 'Core Competencies' section to emphasize missing technical areas."
  ],
  "aiSuggestions": [
    "Learn Docker containerization through hands-on projects and add container orchestration to your portfolio.",
    "Pursue AWS certification (Solutions Architect Associate) to demonstrate cloud infrastructure knowledge.",
    "Build a microservices project using Spring Boot, Docker, and deploy to AWS to showcase modern architecture.",
    "Add 2-3 detailed projects to your GitHub profile demonstrating DevOps and AWS skills.",
    "Consider contributing to open-source projects that use Docker and Kubernetes for real-world experience."
  ],
  "report": "[Detailed Analysis Report]"
}
```

## Test Cases

### Test Case 1: High Match (80%+)
**Resume Skills:** Java, Spring Boot, REST APIs, MySQL, Docker, AWS, Git  
**Job Skills:** Java, Spring Boot, REST APIs, MySQL, Docker

**Expected:**
- `matchPercentage`: ~85%
- `missingSkills`: [] (empty or minimal)
- `suggestions`: Minor refinement suggestions
- `aiSuggestions`: Optimization and advanced skills recommendations

**Test JSON:**
```json
{
  "resumeText": "Senior Backend Engineer with 8 years Java, Spring Boot, REST APIs, MySQL, Docker, AWS, Git expertise. Built microservices, CI/CD pipelines.",
  "jobDescriptionText": "Looking for Backend Engineer with Java, Spring Boot, REST APIs, MySQL, Docker. 5+ years required."
}
```

### Test Case 2: Medium Match (50-80%)
**Resume Skills:** Java, Spring Boot, MySQL  
**Job Skills:** Java, Spring Boot, REST APIs, Docker, AWS, Kubernetes

**Expected:**
- `matchPercentage`: ~60%
- `missingSkills`: REST APIs, Docker, AWS, Kubernetes
- `suggestions`: Improve descriptions and keyword optimization
- `aiSuggestions`: Focus on missing technical skills

**Test JSON:**
```json
{
  "resumeText": "Java Developer with 4 years of Spring Boot development. Built web applications with MySQL databases.",
  "jobDescriptionText": "Backend Engineer needed with Java, Spring Boot, REST APIs, Docker, AWS, Kubernetes experience. Microservices architecture required."
}
```

### Test Case 3: Low Match (<50%)
**Resume Skills:** Python, Django, SQL  
**Job Skills:** Java, Spring Boot, Docker, AWS

**Expected:**
- `matchPercentage`: ~20%
- `missingSkills`: Java, Spring Boot, Docker, AWS
- `suggestions`: Major restructuring and Core Competencies section
- `aiSuggestions`: Complete skill gap analysis and learning path

**Test JSON:**
```json
{
  "resumeText": "Python Developer with 5 years Django and SQL experience. Built data pipelines and web scrapers.",
  "jobDescriptionText": "Java Backend Engineer with Spring Boot, Docker, AWS, and microservices architecture experience required."
}
```

### Test Case 4: Exact Match (100%)
**Resume Skills:** Java, Spring Boot, REST APIs, Docker, AWS  
**Job Skills:** Java, Spring Boot, REST APIs, Docker, AWS

**Expected:**
- `matchPercentage`: 100%
- `missingSkills`: [] (empty)
- `suggestions`: Polish and ATS optimization
- `aiSuggestions`: Career advancement suggestions

**Test JSON:**
```json
{
  "resumeText": "Senior Backend Engineer with 7 years of Java, Spring Boot, REST APIs, Docker, and AWS experience. Expert in microservices and cloud-native architecture.",
  "jobDescriptionText": "Senior Backend Engineer required with Java, Spring Boot, REST APIs, Docker, and AWS skills. 5+ years experience."
}
```

## Testing Fallback (No API Key)

### Step 1: Remove/Clear API Key
```bash
# Windows - clear environment variable
set GEMINI_API_KEY=

# PowerShell
$env:GEMINI_API_KEY=""

# Linux/Mac
unset GEMINI_API_KEY
```

### Step 2: Restart Application
```bash
mvn spring-boot:run
```

### Step 3: Make Request
```bash
curl -X POST http://localhost:8081/api/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "Java Developer",
    "jobDescriptionText": "Senior Java Engineer with 5 years experience"
  }'
```

### Step 4: Verify
✓ Application works normally  
✓ `suggestions` field has rule-based suggestions  
✓ `aiSuggestions` is empty array `[]`  
✓ Log shows: `"Gemini API key not configured. Skipping LLM suggestions."`

## Testing With Postman

### Setup
1. Import into Postman:
   - **Method:** POST
   - **URL:** `http://localhost:8081/api/analyze`
   - **Headers:** `Content-Type: application/json`

### Request Body
```json
{
  "resumeText": "Java developer with 5 years Spring Boot experience",
  "jobDescriptionText": "Senior Backend Engineer with 5+ years Java, Docker, Kubernetes, AWS"
}
```

### Expected Response Status
- **200 OK** - Always (even if API fails, rule-based suggestions returned)

## Logging Verification

### Check Logs for Success
```
com.resumeanalyzer.ai.GeminiSuggestionService : Successfully generated X AI suggestions
```

### Check Logs for API Key Missing
```
com.resumeanalyzer.ai.GeminiSuggestionService : WARN - Gemini API key not configured. Skipping LLM suggestions.
```

### Check Logs for API Error
```
com.resumeanalyzer.ai.GeminiSuggestionService : ERROR - Error calling Gemini API, falling back to rule-based suggestions: [error message]
```

## Performance Testing

### Load Test (Optional)
Send 100 requests sequentially and measure response time.

**With API Key:**
- Expected time: 2-5 seconds per request (Gemini API call + parsing)
- `aiSuggestions` populated

**Without API Key:**
- Expected time: <500ms (rule-based only)
- `aiSuggestions` empty

## Debugging Tips

### Enable Debug Logging
Add to `application.properties`:
```properties
logging.level.com.resumeanalyzer.ai=DEBUG
logging.level.org.springframework.web=DEBUG
```

### Check API Key Configuration
```bash
# Windows
echo %GEMINI_API_KEY%

# PowerShell
echo $env:GEMINI_API_KEY

# Linux/Mac
echo $GEMINI_API_KEY
```

### Test API Key Directly
```bash
curl -X POST https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=YOUR_API_KEY \
  -H "Content-Type: application/json" \
  -d '{
    "contents": [{
      "parts": [{"text": "Hello"}]
    }]
  }'
```

## Success Criteria

- [ ] Application starts without errors
- [ ] `/api/analyze` endpoint returns 200 OK
- [ ] `suggestions` field always populated (rule-based)
- [ ] `aiSuggestions` field present in response (empty without API key)
- [ ] Without API key: `aiSuggestions` is `[]`
- [ ] With API key: `aiSuggestions` contains 1-5 items
- [ ] No 500 errors even if API fails
- [ ] Logs indicate graceful fallback if API unavailable
- [ ] Frontend can access both suggestion types
- [ ] Response JSON is valid and parseable

## Common Issues & Fixes

| Issue | Fix |
|-------|-----|
| `aiSuggestions` always empty | Verify `GEMINI_API_KEY` is set; restart app |
| API timeout (10+ seconds) | Check internet; Gemini API may be slow |
| 400 Bad Request | Check JSON formatting in request body |
| 500 Server Error | Check logs for exception; verify API key format |
| Response parsing fails | Check Gemini API response format hasn't changed |

## Environment Setup for Testing

### Option 1: Environment Variable (Recommended)
```bash
# Set temporarily (session only)
set GEMINI_API_KEY=your-key

# Or set permanently (Windows)
setx GEMINI_API_KEY your-key
```

### Option 2: application.properties
Edit `src/main/resources/application.properties`:
```properties
gemini.api.key=your-api-key-here
```
⚠️ Never commit actual keys to Git!

### Option 3: application-local.properties (Best for Dev)
Create `src/main/resources/application-local.properties`:
```properties
gemini.api.key=your-api-key-here
```

Add to `.gitignore`:
```
application-local.properties
```

Run with profile:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

## Final Verification Checklist

```
Functionality:
  [ ] Build succeeds: mvn clean compile
  [ ] App starts: mvn spring-boot:run
  [ ] Endpoint works: POST /api/analyze
  [ ] Rule-based suggestions always returned
  [ ] AI suggestions conditional on API key

Error Handling:
  [ ] No API key → graceful fallback
  [ ] API timeout → logged and handled
  [ ] Invalid response → fallback triggered
  [ ] Network error → application continues

Response Format:
  [ ] JSON valid and parseable
  [ ] matchPercentage is number 0-100
  [ ] matchedSkills is array
  [ ] missingSkills is array
  [ ] suggestions is array
  [ ] aiSuggestions is array
  [ ] report is string

Performance:
  [ ] Without API key: <500ms response
  [ ] With API key: 2-5s response
  [ ] No memory leaks
  [ ] Handles concurrent requests
```
