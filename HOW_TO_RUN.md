# How to Run the Resume Analyzer Application

## ✅ Quick Start (5 minutes)

### Step 1: Navigate to Project
```bash
cd "/mnt/d/College project/Resume analyser"
```

### Step 2: Build the Project
```bash
mvn clean package
```

This will:
- ✅ Compile all code
- ✅ Run all tests (90+)
- ✅ Create JAR file
- ✅ Takes ~3-4 minutes

Expected output:
```
BUILD SUCCESS
```

### Step 3: Run the Application
```bash
java -jar target/resume-analyzer-1.0.0.jar
```

Expected output:
```
Started ResumeAnalyzerApplication in X seconds
```

### Step 4: Access the Application
Open your browser and go to:
```
http://localhost:8080/swagger-ui.html
```

---

## 📋 Detailed Instructions

### Verify Build Only (Without Running)
```bash
mvn clean compile -DskipTests
```
- Takes ~1 minute
- Shows if code has errors
- Doesn't run tests

### Run All Tests
```bash
mvn test
```
- Runs 90+ test cases
- Verifies all functionality
- Takes ~2-3 minutes

### Run Specific Test Suite
```bash
# Test Job Matching
mvn test -Dtest=JobMatchingServiceTest

# Test Analytics
mvn test -Dtest=AnalyticsServiceTest

# Test Controllers
mvn test -Dtest=AnalyticsControllerTest
```

### Run Application with Logging
```bash
java -jar target/resume-analyzer-1.0.0.jar --debug
```

---

## 🌐 Access Points

### Swagger UI (Interactive API Documentation)
```
http://localhost:8080/swagger-ui.html
```
- Browse all endpoints
- Test API calls interactively
- See request/response examples

### REST API Base
```
http://localhost:8080/api
```

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

---

## 🔐 Authentication

To use the API, you need a JWT token.

### Get Authentication Token
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password"
  }'
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "..."
}
```

### Use Token in Requests
```bash
curl -X GET "http://localhost:8080/api/analytics/dashboard" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

---

## 🧪 Test the New Features

### Task 12: Job Matching

#### Get Recommendations
```bash
curl -X GET "http://localhost:8080/api/job-matching/recommendations?limit=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Get Quality Distribution
```bash
curl -X GET "http://localhost:8080/api/job-matching/quality-distribution" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Get Top Skills in Demand
```bash
curl -X GET "http://localhost:8080/api/job-matching/top-skills?limit=20" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Task 13: Analytics & Reporting

#### Get User Dashboard
```bash
curl -X GET "http://localhost:8080/api/analytics/dashboard" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Get Match Distribution
```bash
curl -X GET "http://localhost:8080/api/analytics/match-distribution" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Get Skill Analytics
```bash
curl -X GET "http://localhost:8080/api/analytics/skills" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Get 30-Day Trends
```bash
curl -X GET "http://localhost:8080/api/analytics/trends/30-days" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Export Analytics (Admin Only)
```bash
curl -X GET "http://localhost:8080/api/analytics/admin-dashboard" \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

#### Export User Data
```bash
curl -X GET "http://localhost:8080/api/analytics/export" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -o analytics.json
```

---

## 📊 Database

The application uses H2 (in-memory) database by default for testing.

### Access H2 Console
```
http://localhost:8080/h2-console
```

To use PostgreSQL in production:
1. Update `application.properties`
2. Set database URL, username, password
3. Restart application

---

## 🐛 Troubleshooting

### Issue: Port 8080 already in use
**Solution:**
```bash
java -jar target/resume-analyzer-1.0.0.jar --server.port=8081
```

### Issue: Build fails with Java version error
**Solution:**
Ensure you have Java 17+ installed:
```bash
java -version
```

### Issue: Dependencies not downloading
**Solution:**
Clear Maven cache:
```bash
mvn clean
rm -rf ~/.m2/repository
mvn compile
```

### Issue: Tests fail
**Solution:**
Check logs:
```bash
mvn test -X
```

---

## 📚 Documentation

All features are documented:

**Task 12 Documentation:**
```
docs/TASK_12_JOB_MATCHING.md
```
- Algorithm explanation
- All endpoints documented
- Usage examples
- Test coverage details

**Task 13 Documentation:**
```
docs/TASK_13_ANALYTICS_REPORTING.md
```
- Feature overview
- All endpoints documented
- API response examples
- Metrics explanation

**Completion Summary:**
```
docs/TASKS_12_13_COMPLETION_SUMMARY.md
```
- Project status
- Code statistics
- Test coverage

**Overnight Report:**
```
OVERNIGHT_COMPLETION_REPORT.txt
```
- Task completion details
- Features list
- Verification checklist

---

## ✅ Verification Checklist

After running, verify:

- [ ] Application starts without errors
- [ ] Swagger UI accessible at http://localhost:8080/swagger-ui.html
- [ ] Can authenticate and get JWT token
- [ ] Can call Job Matching endpoints
- [ ] Can call Analytics endpoints
- [ ] Dashboard shows metrics
- [ ] Data export works
- [ ] Tests pass (mvn test)

---

## 📈 Project Status

✅ **13 of 16 Tasks Complete (81.25%)**

- ✅ Task 1-13: Complete and tested
- ⏳ Task 14-16: Ready to start

**Total Lines of Code:** 18,000+  
**Total Test Cases:** 90+  
**Build Status:** ✅ SUCCESS  

---

## 🚀 Next Steps

When ready to proceed:

### Task 14: Performance Optimization
- Elasticsearch integration
- Redis caching
- Query optimization

### Task 15: DevOps & Deployment
- Docker containerization
- CI/CD pipeline

### Task 16: Security Hardening
- HTTPS/SSL setup
- Rate limiting
- Security headers

---

## 📞 Support

For issues or questions:
1. Check the documentation files
2. Review the code comments
3. Check git history: `git log --oneline`
4. Run tests to verify functionality

---

**Application Ready to Use! 🎉**

Built with ❤️ by AI Assistant  
Last Updated: February 2, 2026
