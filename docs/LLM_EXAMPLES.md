# Example: Testing the LLM Integration

## Scenario: Test with a Real Resume & Job Description

### Example 1: Junior Developer (Low Match)

#### Setup
```bash
# Set Gemini API Key
set GEMINI_API_KEY=your-api-key-here

# Start application
mvn spring-boot:run
```

#### Request
```bash
curl -X POST http://localhost:8081/api/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "Junior Backend Developer - 1 Year Experience\n\nTechnical Skills:\n- Java (basic)\n- MySQL\n- Basic REST APIs\n\nEducation:\n- BS Computer Science\n\nProjects:\n- Built simple CRUD application in Java\n- Created database schemas for small projects",
    "jobDescriptionText": "Senior Backend Engineer - 5+ Years\n\nRequired Skills:\n- Java 8+ (expert level)\n- Spring Boot & Spring Cloud\n- REST APIs & Microservices\n- Docker & Kubernetes\n- AWS (EC2, S3, RDS)\n- CI/CD Pipelines\n- MySQL & PostgreSQL\n- Message Queues (Kafka/RabbitMQ)\n- Unit Testing & Mockito\n\nResponsibilities:\n- Design and implement scalable microservices\n- Lead code reviews\n- Mentor junior developers\n- Work with DevOps on infrastructure"
  }'
```

#### Expected Response
```json
{
  "matchPercentage": 25.0,
  "matchedSkills": ["Java", "MySQL", "REST APIs"],
  "missingSkills": ["Spring Boot", "Spring Cloud", "Docker", "Kubernetes", "AWS", "CI/CD", "PostgreSQL", "Kafka", "RabbitMQ", "Mockito"],
  "suggestions": [
    "Add hands-on experience with Spring Boot to your resume.",
    "Add hands-on experience with Spring Cloud to your resume.",
    "Add hands-on experience with Docker to your resume.",
    "Your resume needs major alignment with job requirements. Restructure to highlight required skills more prominently.",
    "Consider adding a 'Core Competencies' section to emphasize missing technical areas."
  ],
  "aiSuggestions": [
    "Master Spring Boot by building 2-3 production-grade microservices and document the architecture decisions.",
    "Learn Docker containerization and Kubernetes orchestration through hands-on projects; deploy to AWS.",
    "Gain AWS expertise by getting the Solutions Architect Associate certification and building cloud-native applications.",
    "Implement CI/CD pipelines using Jenkins or GitHub Actions and add to your GitHub portfolio.",
    "Study Kafka or RabbitMQ by building message-driven microservices and publishing the code open-source."
  ],
  "report": "Skill Match Analysis Report\n==============================\nJob Match Percentage: 25.0%\n\nMatched Skills (3):\n  ✓ Java\n  ✓ MySQL\n  ✓ REST APIs\n\nMissing Skills (10):\n  ✗ Spring Boot\n  ✗ Spring Cloud\n  ✗ Docker\n  ✗ Kubernetes\n  ✗ AWS\n  ✗ CI/CD\n  ✗ PostgreSQL\n  ✗ Kafka\n  ✗ RabbitMQ\n  ✗ Mockito\n"
}
```

### Example 2: Mid-Level Developer (Medium Match)

#### Request
```bash
curl -X POST http://localhost:8081/api/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "Backend Engineer - 4 Years Experience\n\nCore Skills:\n- Java 8, Java 11\n- Spring Boot 2.x\n- REST APIs (design & implementation)\n- MySQL & PostgreSQL\n- Git & GitHub\n- JUnit & Mockito\n- Maven & Gradle\n\nExperience:\n- Built REST APIs using Spring Boot\n- Designed database schemas\n- Worked with relational databases\n- Some experience with Linux\n\nEducation:\n- BS Computer Science",
    "jobDescriptionText": "Senior Backend Engineer (5+ Years)\n\nMust Have:\n- Java 8+ (expert)\n- Spring Boot & REST APIs\n- Microservices architecture\n- Docker & container basics\n- SQL (MySQL/PostgreSQL)\n- Git version control\n- Unit testing\n\nNice to Have:\n- Kubernetes\n- AWS experience\n- Message queues\n- CI/CD pipelines\n- Spring Cloud\n- MongoDB\n\nResponsibilities:\n- Design scalable backend systems\n- Lead technical discussions\n- Mentor junior developers"
  }'
```

#### Expected Response
```json
{
  "matchPercentage": 70.0,
  "matchedSkills": ["Java", "Spring Boot", "REST APIs", "MySQL", "PostgreSQL", "Git", "JUnit", "Mockito"],
  "missingSkills": ["Microservices", "Docker", "Kubernetes", "AWS", "Message queues", "CI/CD", "Spring Cloud", "MongoDB"],
  "suggestions": [
    "Add hands-on experience with Microservices to your resume.",
    "Add hands-on experience with Docker to your resume.",
    "Improve project descriptions to better highlight relevant skills mentioned in the job posting.",
    "Optimize resume keywords to match ATS scanning patterns used by recruiters."
  ],
  "aiSuggestions": [
    "Build a microservices architecture project using Spring Boot with service-to-service communication and document it on GitHub.",
    "Learn Docker containerization by containerizing one of your existing projects and pushing to Docker Hub.",
    "Get hands-on with Kubernetes by deploying a microservices application to a managed K8s cluster (GKE or EKS).",
    "Implement CI/CD pipelines using GitHub Actions or GitLab CI for automatic testing and deployment.",
    "Add AWS EC2, S3, and RDS experience by migrating one of your projects to the cloud."
  ],
  "report": "Skill Match Analysis Report\n==============================\nJob Match Percentage: 70.0%\n\nMatched Skills (8):\n  ✓ Java\n  ✓ Spring Boot\n  ✓ REST APIs\n  ✓ MySQL\n  ✓ PostgreSQL\n  ✓ Git\n  ✓ JUnit\n  ✓ Mockito\n\nMissing Skills (8):\n  ✗ Microservices\n  ✗ Docker\n  ✗ Kubernetes\n  ✗ AWS\n  ✗ Message queues\n  ✗ CI/CD\n  ✗ Spring Cloud\n  ✗ MongoDB\n"
}
```

### Example 3: Senior Developer (High Match)

#### Request
```bash
curl -X POST http://localhost:8081/api/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "Senior Backend Engineer - 7 Years Experience\n\nExpertise:\n- Java 8, 11, 17 (expert)\n- Spring Boot 2.x & 3.x (expert)\n- Microservices architecture\n- REST APIs & GraphQL\n- MySQL, PostgreSQL, MongoDB\n- Docker & Kubernetes\n- AWS (EC2, S3, RDS, Lambda)\n- CI/CD (Jenkins, GitHub Actions)\n- Spring Cloud (Eureka, Config Server)\n- Kafka & RabbitMQ\n- Git & GitHub\n- JUnit, Mockito, TestNG\n- Maven, Gradle\n- Agile/Scrum\n\nAchievements:\n- Designed and built microservices architecture\n- Led team of 3 junior developers\n- Improved API performance by 40%\n- Mentored interns in Java best practices",
    "jobDescriptionText": "Senior Backend Engineer (5+ Years)\n\nRequired:\n- Java 8+ (expert)\n- Spring Boot\n- Microservices\n- Docker\n- SQL (MySQL/PostgreSQL)\n- REST APIs\n- Git\n- Testing\n\nDesired:\n- Kubernetes\n- AWS\n- Message queues\n- CI/CD\n- Spring Cloud\n- NoSQL\n\nResponsibilities:\n- Design backend systems\n- Lead code reviews\n- Mentor developers"
  }'
```

#### Expected Response
```json
{
  "matchPercentage": 95.0,
  "matchedSkills": ["Java", "Spring Boot", "Microservices", "Docker", "MySQL", "PostgreSQL", "REST APIs", "Git", "JUnit", "Mockito", "Kubernetes", "AWS", "Message queues", "CI/CD", "Spring Cloud", "MongoDB"],
  "missingSkills": [],
  "suggestions": [
    "Your resume has strong alignment. Refine formatting and ATS keyword optimization.",
    "Consider adding quantifiable results (e.g., performance improvements) to strengthen impact."
  ],
  "aiSuggestions": [
    "Highlight quantifiable impact metrics: specify performance improvements (40%), reliability gains, and cost savings from your projects.",
    "Create a brief 'Technical Leadership' section documenting your mentoring contributions and team impact.",
    "Consider publishing technical blog posts or conference talks about microservices architecture or cloud patterns.",
    "Add cloud certifications or advanced training to your resume to demonstrate continuous learning.",
    "Include open-source contributions or personal projects demonstrating advanced architectural patterns."
  ],
  "report": "Skill Match Analysis Report\n==============================\nJob Match Percentage: 95.0%\n\nMatched Skills (16):\n  ✓ Java\n  ✓ Spring Boot\n  ✓ Microservices\n  ✓ Docker\n  ✓ MySQL\n  ✓ PostgreSQL\n  ✓ REST APIs\n  ✓ Git\n  ✓ JUnit\n  ✓ Mockito\n  ✓ Kubernetes\n  ✓ AWS\n  ✓ Message queues\n  ✓ CI/CD\n  ✓ Spring Cloud\n  ✓ MongoDB\n\nMissing Skills (0):\n  (None - Perfect match!)"
}
```

## Observing the Integration in Action

### Check Logs While Processing

When processing with API key, you should see (in application console):

```
2025-12-27 12:50:15.234 DEBUG [AI Resume Analyzer] [http-nio-8081-exec-1] c.r.a.GeminiSuggestionService : Calling Gemini API for resume analysis
2025-12-27 12:50:18.567 DEBUG [AI Resume Analyzer] [http-nio-8081-exec-1] c.r.a.GeminiSuggestionService : Successfully generated 5 AI suggestions
```

Without API key, you should see:

```
2025-12-27 12:50:15.234 WARN [AI Resume Analyzer] [http-nio-8081-exec-1] c.r.a.GeminiSuggestionService : Gemini API key not configured. Skipping LLM suggestions.
```

## Testing Both Suggestion Types Side-by-Side

### Parse Response to Compare

```bash
# Run request and pretty-print JSON
curl -s -X POST http://localhost:8081/api/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "resumeText": "Java Developer with 3 years Spring Boot experience",
    "jobDescriptionText": "Senior Backend Engineer with 5+ years Java, Docker, AWS"
  }' | python -m json.tool
```

### Sample Output Structure

```json
{
  "matchPercentage": 50.0,
  
  "matchedSkills": [
    "Java",
    "Spring Boot"
  ],
  
  "missingSkills": [
    "Docker",
    "AWS"
  ],
  
  "suggestions": [
    "Add hands-on experience with Docker to your resume.",
    "Add hands-on experience with AWS to your resume.",
    "Your resume needs major alignment with job requirements. Restructure to highlight required skills more prominently.",
    "Consider adding a 'Core Competencies' section to emphasize missing technical areas."
  ],
  
  "aiSuggestions": [
    "Build hands-on Docker experience by containerizing a Spring Boot application and deploying it to a container registry.",
    "Gain AWS expertise by deploying a Docker container to AWS ECS or EKS, and document the setup.",
    "Complete an AWS Solutions Architect Associate certification to demonstrate cloud infrastructure knowledge.",
    "Create a microservices project using Spring Boot and Docker to show modern architecture understanding.",
    "Contribute to open-source projects that use Docker and Kubernetes for real-world experience."
  ],
  
  "report": "[Detailed formatted report]"
}
```

## Key Observations

1. **Rule-Based Suggestions** (always present):
   - Generic, based on skill gaps
   - Deterministic (same input = same output)
   - Fast (<100ms)

2. **AI Suggestions** (when API key configured):
   - Specific and actionable
   - Context-aware (uses actual resume/job text)
   - More natural language
   - Takes 2-5 seconds (API latency)

3. **Fallback Behavior**:
   - Without API key: rule-based only
   - API timeout: rule-based only
   - API error: rule-based only
   - **Never crashes**

## Integration Complete! ✅

You now have:
- ✓ Rule-based suggestions (always available)
- ✓ AI-powered suggestions (when configured)
- ✓ Graceful fallback (if API unavailable)
- ✓ Production-ready code
- ✓ Comprehensive documentation
