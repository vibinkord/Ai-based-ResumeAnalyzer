# Quick Start - LLM Integration (5 Minutes)

## Step 1: Get API Key (2 minutes)

1. Visit: https://ai.google.dev/
2. Click "Get API Key"
3. Sign in with Google
4. Create new API key
5. Copy the key

## Step 2: Set Environment Variable (1 minute)

### Windows (Command Prompt)
```cmd
set GEMINI_API_KEY=paste-your-key-here
```

### Windows (PowerShell)
```powershell
$env:GEMINI_API_KEY="paste-your-key-here"
```

### Linux/Mac
```bash
export GEMINI_API_KEY="paste-your-key-here"
```

## Step 3: Start Application (1 minute)

```bash
cd "d:\College Project\Resume analyser"
mvn spring-boot:run
```

Wait for: `Tomcat initialized with port 8081`

## Step 4: Test It (1 minute)

### Using cURL
```bash
curl -X POST http://localhost:8081/api/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "Java Developer with 3 years Spring Boot experience",
    "jobDescriptionText": "Senior Backend Engineer with 5+ years Java, Docker, AWS experience"
  }'
```

### Expected Response
```json
{
  "matchPercentage": 50.0,
  "matchedSkills": ["Java", "Spring Boot"],
  "missingSkills": ["Docker", "AWS"],
  "suggestions": [
    "Add hands-on experience with Docker to your resume.",
    "Add hands-on experience with AWS to your resume.",
    ...
  ],
  "aiSuggestions": [
    "Learn Docker containerization and add a project to your portfolio.",
    "Gain AWS expertise through hands-on projects and certification.",
    ...
  ],
  "report": "[Full Analysis Report]"
}
```

✅ **Done!** Both rule-based and AI suggestions are working!

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `aiSuggestions` empty | Verify API key set: `echo %GEMINI_API_KEY%` |
| App won't start | Kill process on port 8081: `taskkill /PID [pid] /F` |
| 400 Bad Request | Check JSON formatting (use proper quotes) |
| Timeout errors | Check internet connection |

## Full Documentation

- **Setup Guide:** `docs/LLM_INTEGRATION_GUIDE.md`
- **Testing Guide:** `docs/LLM_TESTING_GUIDE.md`
- **Examples:** `docs/LLM_EXAMPLES.md`
- **Architecture:** `docs/LLM_INTEGRATION_SUMMARY.md`

## API Endpoint

```
POST /api/analyze
Content-Type: application/json

{
  "resumeText": "Your resume text here",
  "jobDescriptionText": "Job description text here"
}
```

**Response includes:**
- `matchPercentage` - Skill match (0-100%)
- `matchedSkills` - Common skills
- `missingSkills` - Skills needed
- `suggestions` - Rule-based suggestions
- `aiSuggestions` - **AI-powered suggestions** ✨
- `report` - Formatted analysis report

## Key Points

✅ Works without API key (rule-based suggestions only)  
✅ Graceful fallback if API fails  
✅ Never crashes the application  
✅ Returns AI suggestions when API is available  
✅ All code compiled and tested  

---

**You're all set!** 🚀

For more details, see `docs/LLM_INTEGRATION_GUIDE.md`
