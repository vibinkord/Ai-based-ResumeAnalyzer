# LLM Integration Guide - AI Resume Analyzer

## Overview
The Resume Analyzer now features AI-powered suggestions via Google Gemini API. The system generates both:
- **Rule-based suggestions**: Deterministic logic based on skill matching (always available)
- **AI-enhanced suggestions**: Natural language improvements from Gemini LLM (when API key is configured)

## Configuration

### Step 1: Get a Gemini API Key
1. Visit: https://ai.google.dev/
2. Click "Get API Key"
3. Sign in with your Google account
4. Create a new API key
5. Copy the key

### Step 2: Configure the Application
Set the API key as an environment variable:

**Windows (Command Prompt):**
```cmd
set GEMINI_API_KEY=your-api-key-here
```

**Windows (PowerShell):**
```powershell
$env:GEMINI_API_KEY="your-api-key-here"
```

**Linux/Mac:**
```bash
export GEMINI_API_KEY="your-api-key-here"
```

**Or directly in application.properties:**
```properties
gemini.api.key=your-api-key-here
```

## Architecture

### GeminiSuggestionService
**Location:** `com.resumeanalyzer.ai.GeminiSuggestionService`

**Responsibilities:**
1. Builds a structured prompt with resume, job description, and skill analysis
2. Calls Google Gemini API via REST (HttpClient)
3. Parses JSON response to extract suggestions
4. Returns up to 5 actionable AI suggestions
5. Gracefully fails and logs errors if API is unavailable

**Method Signature:**
```java
public List<String> generateAISuggestions(
    String resumeText,
    String jobDescriptionText,
    Set<String> matchedSkills,
    Set<String> missingSkills,
    double matchPercentage
)
```

### Error Handling & Fallback
- If API key is missing → logs warning, returns empty list
- If API call fails → logs error, returns empty list
- If response parsing fails → returns empty list
- **Application never crashes** - frontend receives rule-based suggestions only

## Example Prompt Sent to Gemini

```
You are a senior resume reviewer and career coach. Analyze the resume against the job description and provide ONLY 5 or fewer specific, actionable bullet-point suggestions.

RESUME:
[Resume text here]

JOB DESCRIPTION:
[Job description text here]

ANALYSIS RESULTS:
- Match Percentage: 65.0%
- Matched Skills: [Java, Spring Boot, REST APIs, MySQL]
- Missing Skills: [Docker, Kubernetes, AWS]

INSTRUCTIONS:
1. Focus on the missing skills and how to acquire or highlight relevant experience.
2. Provide concise, specific recommendations (avoid generic advice).
3. Prioritize actionable improvements that directly address job requirements.
4. Format output as ONLY bullet points with '-' prefix, one per line.
5. Do NOT include explanations, preamble, or numbering.
6. Do NOT exceed 5 bullet points.
7. Do NOT include markdown formatting.

RESUME SUGGESTIONS:
```

## Example Response

### Request (JSON)
```json
{
  "resumeText": "Senior Java Developer with 5 years experience in Spring Boot...",
  "jobDescriptionText": "We're looking for a Java developer with Docker and Kubernetes experience..."
}
```

### Response (JSON)
```json
{
  "matchPercentage": 65.0,
  "matchedSkills": ["Java", "Spring Boot", "REST APIs", "MySQL"],
  "missingSkills": ["Docker", "Kubernetes", "AWS"],
  "suggestions": [
    "Add hands-on experience with Docker to your resume.",
    "Add hands-on experience with Kubernetes to your resume.",
    "Improve project descriptions to better highlight relevant skills mentioned in the job posting.",
    "Optimize resume keywords to match ATS scanning patterns used by recruiters."
  ],
  "aiSuggestions": [
    "Gain hands-on experience with Docker containerization and add a project to your portfolio.",
    "Learn Kubernetes orchestration through a cloud platform (GKE or EKS) and document the experience.",
    "Add AWS certifications or projects to strengthen cloud infrastructure skills.",
    "Highlight any microservices architecture work prominently in your experience section.",
    "Consider contributing to open-source Kubernetes projects to demonstrate expertise."
  ],
  "report": "..."
}
```

## Response Structure

The `/api/analyze` endpoint now returns:

| Field | Type | Description |
|-------|------|-------------|
| `matchPercentage` | double | Percentage of skills matched (0-100) |
| `matchedSkills` | Set<String> | Skills found in both resume and job description |
| `missingSkills` | Set<String> | Skills required but not in resume |
| `suggestions` | List<String> | **Rule-based suggestions** (always present) |
| `aiSuggestions` | List<String> | **AI-generated suggestions** (empty if API fails or not configured) |
| `report` | String | Formatted analysis report |

## Testing the Integration

### Test 1: Without API Key
If `GEMINI_API_KEY` is not set:
- ✓ Application starts normally
- ✓ `/api/analyze` returns only `suggestions` (rule-based)
- ✓ `aiSuggestions` is empty list
- ✓ No errors in logs (only warning about missing API key)

### Test 2: With API Key
If `GEMINI_API_KEY` is configured:
- ✓ Application starts normally
- ✓ `/api/analyze` returns both `suggestions` and `aiSuggestions`
- ✓ `aiSuggestions` contains 1-5 actionable items
- ✓ Logs show successful API calls

### Test 3: API Failure Simulation
If Gemini API is temporarily unavailable:
- ✓ Application continues to work
- ✓ `/api/analyze` returns only `suggestions` (rule-based)
- ✓ Error logged at ERROR level
- ✓ User doesn't experience downtime

## Sample cURL Request

```bash
curl -X POST http://localhost:8081/api/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "Senior Backend Engineer with 7 years in Java, Spring Boot, REST APIs, MySQL, PostgreSQL. Experience with microservices architecture.",
    "jobDescriptionText": "Looking for Backend Engineer with Java, Spring Boot, Docker, Kubernetes, AWS experience. 5+ years required. Microservices preferred."
  }'
```

## Integration Points

### 1. ResumeAnalysisController
- Autowires `GeminiSuggestionService`
- Calls service after rule-based analysis
- Includes AI suggestions in response DTO

### 2. ResumeAnalysisResponse DTO
- Added field: `List<String> aiSuggestions`
- Added constructor: `ResumeAnalysisResponse(..., aiSuggestions, ...)`
- Getters/setters for AI suggestions

### 3. application.properties
- New property: `gemini.api.key`
- Reads from environment variable: `GEMINI_API_KEY`
- Defaults to empty string if not set

## Security Notes
- API keys should NEVER be hardcoded in source code
- Always use environment variables for secrets
- Git ignores should exclude `.env` files
- Consider using Spring Cloud Config for sensitive configurations in production

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| `aiSuggestions` is always empty | API key not configured | Set `GEMINI_API_KEY` environment variable |
| Error: "RestClient error calling Gemini API" | Network issue or API unavailable | Check internet connection; API may be down |
| Error: "Error parsing Gemini API response" | Unexpected API response format | Check API documentation; log response body |
| Build fails with Jackson errors | Missing JSON parser dependency | Check `pom.xml` has jackson-databind |

## Next Steps

### Frontend Integration (Not Done Yet)
The frontend (`index.html`, `app.js`) currently displays only `suggestions`. To display AI suggestions:
1. Update `app.js` to access `response.aiSuggestions`
2. Add UI section to display AI suggestions separately
3. Optional: Add visual indicator (e.g., "AI-Powered" badge)

### Prompt Optimization
To improve suggestion quality:
1. Adjust system prompt in `buildPrompt()` method
2. Experiment with `temperature` parameter (0.7 default)
3. Test with various resumes and job descriptions
4. A/B test prompt variations

### Cost Considerations
- Gemini API has free tier (generous for testing)
- Consider caching AI suggestions for identical inputs
- Monitor API usage in production

## Code References

**GeminiSuggestionService:**
- Location: `src/main/java/com/resumeanalyzer/ai/GeminiSuggestionService.java`
- Handles all LLM communication
- Independent of rule-based engine

**ResumeAnalysisController:**
- Updated to call `GeminiSuggestionService.generateAISuggestions()`
- Falls back gracefully if service unavailable

**ResumeAnalysisResponse:**
- Added `aiSuggestions` field
- Backward compatible (existing fields unchanged)
