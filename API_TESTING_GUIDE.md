# AI Resume Analyzer - API Testing Guide

## ✅ Web Stage W1 Complete | Web Stages Remaining: 3

---

## 🎯 Quick Start

The Spring Boot REST API is now running and exposing the resume analysis engine via HTTP endpoints.

### Starting the Application

```bash
# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using packaged JAR (after mvn package)
java -jar target/resume-analyzer-1.0.0.jar
```

The application will start on **http://localhost:8080**

---

## 📡 API Endpoint

### POST /api/analyze

Analyzes a resume against a job description and returns match results, suggestions, and a detailed report.

**URL**: `http://localhost:8080/api/analyze`  
**Method**: `POST`  
**Content-Type**: `application/json`

---

## 📥 Request Format

```json
{
  "resumeText": "Your full resume text here...",
  "jobDescriptionText": "Job description text here..."
}
```

### Request Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `resumeText` | String | ✅ Yes | The complete text of the resume to analyze |
| `jobDescriptionText` | String | ❌ No | The job description to match against (optional) |

---

## 📤 Response Format

```json
{
  "matchPercentage": 75.0,
  "matchedSkills": ["Java", "REST", "SQL"],
  "missingSkills": ["Spring", "Docker", "Kubernetes"],
  "suggestions": [
    "Add hands-on experience with Spring to your resume.",
    "Add hands-on experience with Docker to your resume.",
    "Add hands-on experience with Kubernetes to your resume.",
    "Improve project descriptions to better highlight relevant skills mentioned in the job posting."
  ],
  "report": "==============================\nAI Resume Analysis Report\n=============================="
}
```

### Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `matchPercentage` | Double | Overall match score (0-100%) |
| `matchedSkills` | Array | Skills found in both resume and job description |
| `missingSkills` | Array | Skills from job description not found in resume |
| `suggestions` | Array | AI-generated improvement suggestions |
| `report` | String | Formatted text report with full analysis |

---

## 🧪 Testing Examples

### Example 1: Using cURL (Command Line)

```bash
curl -X POST http://localhost:8080/api/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "I am a software developer with 5 years of experience in Java, Spring Boot, SQL, REST APIs, Git, and Microservices architecture.",
    "jobDescriptionText": "We are looking for a Senior Java Developer with expertise in Java, Spring Boot, SQL, REST, Microservices, Docker, and Kubernetes."
  }'
```

**Expected Response**:
```json
{
  "matchPercentage": 71.43,
  "matchedSkills": ["Java", "Spring", "REST", "SQL", "Microservices"],
  "missingSkills": ["Docker", "Kubernetes"],
  "suggestions": [
    "Add hands-on experience with Docker to your resume.",
    "Add hands-on experience with Kubernetes to your resume.",
    "Improve project descriptions to better highlight relevant skills mentioned in the job posting.",
    "Optimize resume keywords to match ATS scanning patterns used by recruiters."
  ],
  "report": "==============================\nAI Resume Analysis Report\n==============================\n\nResume Match Score: 71.43%\n\nMatched Skills:\n- Java\n- Spring\n- REST\n- SQL\n- Microservices\n\nMissing Skills:\n- Docker\n- Kubernetes\n\nSuggestions:\n- Add hands-on experience with Docker to your resume.\n- Add hands-on experience with Kubernetes to your resume.\n- Improve project descriptions to better highlight relevant skills mentioned in the job posting.\n- Optimize resume keywords to match ATS scanning patterns used by recruiters.\n"
}
```

---

### Example 2: Using cURL (Windows PowerShell)

```powershell
curl.exe -X POST http://localhost:8080/api/analyze `
  -H "Content-Type: application/json" `
  -d '{\"resumeText\":\"I have Java, SQL, OOP, REST, Git experience\",\"jobDescriptionText\":\"Looking for Java, Spring Boot, SQL, REST, Docker, Kubernetes skills\"}'
```

---

### Example 3: Using Postman

1. **Open Postman**
2. **Create a new POST request** to `http://localhost:8080/api/analyze`
3. **Set Headers**:
   - Key: `Content-Type`
   - Value: `application/json`
4. **Set Body** (select "raw" and "JSON"):
   ```json
   {
     "resumeText": "Software Engineer with experience in Java, Python, SQL, REST APIs, Git, Docker, and AWS cloud services.",
     "jobDescriptionText": "Seeking a Full Stack Developer proficient in Java, Spring Boot, React, SQL, Docker, Kubernetes, and AWS."
   }
   ```
5. **Click Send**

---

### Example 4: Using JavaScript (Fetch API)

```javascript
fetch('http://localhost:8080/api/analyze', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    resumeText: "Full Stack Developer with 3 years experience. Skills: Java, JavaScript, React, Node.js, SQL, Git, Docker.",
    jobDescriptionText: "Looking for Full Stack Developer: Java, Spring Boot, React, PostgreSQL, Docker, Kubernetes, CI/CD."
  })
})
.then(response => response.json())
.then(data => {
  console.log('Match Percentage:', data.matchPercentage);
  console.log('Matched Skills:', data.matchedSkills);
  console.log('Missing Skills:', data.missingSkills);
  console.log('Suggestions:', data.suggestions);
})
.catch(error => console.error('Error:', error));
```

---

### Example 5: Using Python (requests library)

```python
import requests
import json

url = "http://localhost:8080/api/analyze"
headers = {"Content-Type": "application/json"}

payload = {
    "resumeText": "DevOps Engineer: 4 years exp. Skills include Jenkins, Docker, Kubernetes, AWS, Python, Bash scripting, Git, CI/CD pipelines.",
    "jobDescriptionText": "DevOps Engineer needed: Kubernetes, Docker, AWS, Terraform, Jenkins, Python, monitoring tools (Prometheus, Grafana)."
}

response = requests.post(url, headers=headers, data=json.dumps(payload))

if response.status_code == 200:
    result = response.json()
    print(f"Match: {result['matchPercentage']}%")
    print(f"Matched: {result['matchedSkills']}")
    print(f"Missing: {result['missingSkills']}")
    print(f"\nSuggestions:")
    for suggestion in result['suggestions']:
        print(f"  - {suggestion}")
else:
    print(f"Error: {response.status_code}")
```

---

## 🔍 How It Works

The API internally uses the same 5-stage analysis engine:

1. **Stage 2 - Skill Extraction**: Extracts technical skills from resume and job description using token-based matching
2. **Stage 3 - Skill Matching**: Compares skills and calculates match percentage
3. **Stage 4 - Suggestion Generation**: Generates AI-like improvement suggestions based on missing skills and match score
4. **Stage 5 - Report Generation**: Creates a formatted text report with all analysis details

**Key Point**: No business logic is in the controller — it simply orchestrates the existing analysis classes.

---

## ✅ Validation Rules

- **resumeText**: Cannot be null or empty (returns 400 Bad Request)
- **jobDescriptionText**: Optional (if missing, only resume skills are extracted with 0% match)
- **Response**: Always returns 200 OK with valid JSON if request is valid

---

## 🐛 Troubleshooting

### Issue: Connection Refused / Cannot Connect

**Solution**: Make sure the Spring Boot application is running:
```bash
mvn spring-boot:run
```
Wait for the message: `Started ResumeAnalyzerApplication in X.XXX seconds`

---

### Issue: 400 Bad Request

**Cause**: Empty or missing `resumeText` field  
**Solution**: Ensure `resumeText` has content:
```json
{
  "resumeText": "Some resume text here",
  "jobDescriptionText": "Some job description"
}
```

---

### Issue: JSON Parse Error

**Cause**: Invalid JSON syntax (missing quotes, commas, brackets)  
**Solution**: Validate JSON using a tool like [jsonlint.com](https://jsonlint.com/)

---

## 📝 Testing Checklist

- [x] Application starts successfully on port 8080
- [x] POST /api/analyze accepts valid JSON request
- [x] Response includes matchPercentage, matchedSkills, missingSkills, suggestions, report
- [x] Empty resumeText returns 400 Bad Request
- [x] Skills are correctly extracted using token-based matching
- [x] Match percentage is calculated accurately
- [x] Suggestions are generated based on missing skills
- [x] Report contains formatted analysis output

---

## 🎓 Next Steps (Remaining Web Stages)

### Web Stage W2: File Upload Support
- Add endpoint to accept PDF/DOCX resume files
- Parse document content into text
- Support both file upload and direct text input

### Web Stage W3: Frontend Integration
- Create HTML/CSS/JavaScript interface
- Real-time analysis results display
- User-friendly file upload and text input

### Web Stage W4: AI Enhancement
- Integrate actual AI/ML models (optional)
- Advanced NLP for better skill detection
- Semantic similarity matching

---

## 📚 Project Structure (Web Layer)

```
src/main/java/com/resumeanalyzer/
  ├── web/
  │   ├── ResumeAnalyzerApplication.java  # Spring Boot main class
  │   ├── controller/
  │   │   └── ResumeAnalysisController.java  # REST controller
  │   └── dto/
  │       ├── AnalyzeRequest.java  # Request DTO
  │       └── AnalyzeResponse.java # Response DTO
  ├── analysis/  # Existing Stage 2-3 logic (unchanged)
  ├── suggestions/  # Existing Stage 4 logic (unchanged)
  ├── report/  # Existing Stage 5 logic (unchanged)
  ├── io/  # Existing Stage 1 logic (unchanged)
  └── util/  # Existing utilities (unchanged)
```

---

## 🎉 Success Metrics

- ✅ Console application (Stages 1-5) fully functional
- ✅ Spring Boot REST API (Stage W1) operational
- ✅ API tested with cURL and returns valid JSON
- ✅ All existing domain logic reused without modification
- ✅ Clean architecture maintained (controller has no business logic)

**Status**: Web Stage W1 Complete 🚀

---

**Last Updated**: 2025-12-27  
**Spring Boot Version**: 3.2.1  
**Java Version**: 17  
**Port**: 8080
