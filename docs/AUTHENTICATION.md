# Task 10: Authentication & Authorization Implementation

## Overview

Task 10 implements comprehensive JWT-based authentication and authorization for the AI Resume Analyzer application using Spring Security. This task adds user login, registration, and role-based access control (RBAC).

## What Was Implemented

### 1. Security Architecture

#### Spring Security Configuration (`SecurityConfig.java`)
- **Stateless JWT Authentication**: Configured for token-based API access (no sessions)
- **Role-Based Access Control (RBAC)**: Supports 4 role types
- **Public Endpoints**: Health checks, API docs, analysis endpoints remain public
- **Protected Endpoints**: User and resume data require authentication
- **Admin Endpoints**: Require ROLE_ADMIN authority

#### JWT Implementation (`JwtTokenProvider.java`)
- **Token Generation**: Create JWT tokens with user claims and roles
- **Token Validation**: Verify token signature and expiration
- **Refresh Tokens**: Generate long-lived tokens for token renewal
- **Claim Extraction**: Extract user information and roles from tokens
- **Expiration Management**: Handle token lifecycle

### 2. User Authentication

#### User Entity Enhancements
- Implements Spring `UserDetails` interface
- Supports multiple roles via `@ElementCollection`
- Automatic audit timestamps
- Helper methods for role checking (isAdmin(), isPremium(), hasRole())

#### Role Enum (`Role.java`)
```java
ROLE_USER      // Standard user access
ROLE_ADMIN     // Administrative privileges
ROLE_PREMIUM   // Premium features access
ROLE_ANALYST   // Advanced analytics access
```

### 3. Authentication Endpoints

#### AuthController (`/api/auth/`)

**POST /api/auth/login**
```json
Request:
{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "email": "user@example.com",
  "fullName": "John Doe",
  "roles": ["ROLE_USER"],
  "expiresIn": 3600000,
  "message": "Login successful",
  "success": true
}
```

**POST /api/auth/register**
```json
Request:
{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "John Doe"
}

Response: (same as login)
```

**POST /api/auth/refresh-token**
```
Request: ?refreshToken=<token>

Response:
{
  "accessToken": "<new-token>",
  "tokenType": "Bearer",
  "expiresIn": 3600000,
  "message": "Token refreshed successfully",
  "success": true
}
```

**POST /api/auth/logout**
```json
Response:
{
  "message": "Logged out successfully",
  "success": true
}
```

### 4. Security Features

#### Password Security
- **BCrypt Encryption**: All passwords hashed with BCrypt
- **Configurable Min Length**: Default 6 characters in dev/test, 8 in production
- **No Plaintext Storage**: Passwords never stored in plain text

#### JWT Security
- **HS512 Algorithm**: HMAC-SHA512 for token signing
- **Configurable Expiration**: 
  - Access Token: 1 hour (dev/test) - configurable via `jwt.expiration`
  - Refresh Token: 7 days (dev/test) - configurable via `jwt.refresh-expiration`
- **Environment Variables**: Secrets managed via environment in production

#### CORS Configuration
- **Development**: All origins allowed (*)
- **Production**: Restricted to configured domains

### 5. Database Support

#### New Table: user_roles (Flyway Migration V3)
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);
```

- Supports many-to-many relationship between users and roles
- Cascade delete when user is deleted
- Indexed for performance

### 6. Configuration

#### Property Files Updated

**application.properties** (main)
```properties
jwt.secret=your-secret-key-min-64-chars
jwt.expiration=3600000          # 1 hour
jwt.refresh-expiration=604800000 # 7 days
```

**application-dev.properties**
```properties
jwt.secret=dev-secret-key-64-chars-minimum
jwt.expiration=3600000
logging.level.org.springframework.security=DEBUG
```

**application-test.properties**
```properties
jwt.secret=test-secret-key-64-chars
jwt.expiration=1800000         # 30 minutes
jwt.refresh-expiration=3600000 # 1 hour
```

**application-prod.properties**
```properties
jwt.secret=${JWT_SECRET}       # From environment
jwt.expiration=${JWT_EXPIRATION:3600000}
jwt.refresh-expiration=${JWT_REFRESH_EXPIRATION:604800000}
```

### 7. Using Authentication

#### Getting a Token

```bash
curl -X POST http://localhost:8084/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

#### Using Token in Requests

```bash
curl -X GET http://localhost:8084/api/resumes \
  -H "Authorization: Bearer <access-token>"
```

#### Refreshing Token

```bash
curl -X POST "http://localhost:8084/api/auth/refresh-token?refreshToken=<refresh-token>"
```

### 8. Endpoint Security

#### Public Endpoints (No Authentication Required)
```
POST   /api/auth/login
POST   /api/auth/register
POST   /api/auth/refresh-token
POST   /api/auth/logout
GET    /health
GET    /actuator/**
GET    /v3/api-docs/**
GET    /swagger-ui/**
POST   /api/v1/analyze          (backward compatibility)
GET    /api/v1/skills           (backward compatibility)
POST   /api/v1/batch-analysis   (backward compatibility)
POST   /api/v1/compare          (backward compatibility)
```

#### Protected Endpoints (Requires Authentication)
```
GET    /api/resumes/**          (any authenticated user)
POST   /api/resumes/**          (any authenticated user)
PUT    /api/resumes/**          (any authenticated user)
GET    /api/analysis/**         (any authenticated user)
GET    /api/users/**            (any authenticated user)
```

#### Admin-Only Endpoints (Requires ROLE_ADMIN)
```
DELETE /api/users/**
GET    /api/admin/**
```

### 9. Test Coverage

#### JwtTokenProviderTest (15 test cases)
- Token generation and validation
- Username extraction
- Token expiration checks
- Custom claims handling
- Invalid token rejection

#### AuthControllerTest (10 test cases)
- Login with valid/invalid credentials
- User registration
- Email validation
- Password validation
- Token refresh
- Logout

### 10. Security Best Practices Implemented

✅ **Password Hashing**: BCrypt with strong algorithms  
✅ **Token Security**: Signed JWT tokens with expiration  
✅ **Stateless Design**: No session storage required  
✅ **CSRF Protection**: Disabled for stateless API (appropriate)  
✅ **Secret Management**: Environment variables for production  
✅ **Role-Based Access**: Multiple role types with granular permissions  
✅ **Input Validation**: Email and password validation on registration  
✅ **Audit Trails**: Timestamps on user creation/update  
✅ **Error Handling**: Generic error messages (no info leakage)  
✅ **CORS Configuration**: Environment-specific settings

## Integration with Existing Code

### Task 9 Integration
- ✅ User entity enhanced with roles support
- ✅ New user_roles table via Flyway V3
- ✅ All 33 Repository Integration Tests still passing
- ✅ UserService compatible with new authentication flow

### Future Task Prerequisites
- Foundation for Task 11 (Email Notifications) - authenticated users
- Support for Task 14 (Cloud Deployment) - environment-based secrets
- Basis for Task 15 (Frontend UI) - JWT token management

## Files Created/Modified

### New Files
- `src/main/java/com/resumeanalyzer/config/SecurityConfig.java`
- `src/main/java/com/resumeanalyzer/controller/AuthController.java`
- `src/main/java/com/resumeanalyzer/security/JwtTokenProvider.java`
- `src/main/java/com/resumeanalyzer/security/JwtAuthenticationFilter.java`
- `src/main/java/com/resumeanalyzer/security/CustomUserDetailsService.java`
- `src/main/java/com/resumeanalyzer/model/entity/Role.java`
- `src/main/java/com/resumeanalyzer/model/dto/LoginRequest.java`
- `src/main/java/com/resumeanalyzer/model/dto/LoginResponse.java`
- `src/main/java/com/resumeanalyzer/model/dto/RegisterRequest.java`
- `src/test/java/com/resumeanalyzer/security/JwtTokenProviderTest.java`
- `src/test/java/com/resumeanalyzer/controller/AuthControllerTest.java`
- `src/main/resources/db/migration/V3__Add_User_Roles.sql`

### Modified Files
- `pom.xml` - Added Spring Security and JJWT dependencies
- `src/main/java/com/resumeanalyzer/model/entity/User.java` - Implements UserDetails
- `src/main/resources/application.properties` - JWT configuration
- `src/main/resources/application-dev.properties` - Dev JWT settings
- `src/main/resources/application-test.properties` - Test JWT settings
- `src/main/resources/application-prod.properties` - Prod JWT settings

## Testing Task 10

### Run All Tests
```bash
mvn clean test
```

### Run Auth Tests Only
```bash
mvn test -Dtest=AuthControllerTest,JwtTokenProviderTest
```

### Run Task 9 Repository Tests (Verification)
```bash
mvn test -Dtest=RepositoryIntegrationTest
```

## Performance Considerations

- **JWT Validation**: O(1) operation using cryptographic verification
- **Database Queries**: Single query per login to load user and roles
- **Password Hashing**: BCrypt is computationally expensive (by design) - ~10 rounds
- **Token Size**: ~500 bytes including claims and signature
- **Refresh Token Flow**: Reduces frequency of password authentication

## Security Hardening Checklist

- [x] Password hashing with BCrypt
- [x] JWT token signing with HS512
- [x] Stateless authentication (no session hijacking)
- [x] CSRF protection configuration
- [x] Role-based access control
- [x] Input validation on auth endpoints
- [x] Error message sanitization
- [x] Environment-based secret management
- [x] Token expiration enforcement
- [x] Refresh token rotation support

## Troubleshooting

### Invalid Token Error
**Cause**: Token signature mismatch or secret key difference  
**Solution**: Ensure same secret key used in all environments

### Token Expired Error  
**Cause**: Token has exceeded expiration time  
**Solution**: Use refresh token endpoint to get new access token

### CORS Origin Not Allowed
**Cause**: Frontend not in allowed origins list  
**Solution**: Update `spring.web.cors.allowed-origins` in properties

### User Not Found Error
**Cause**: Email doesn't exist in database  
**Solution**: Register new user first or check email spelling

## Next Steps (Task 11+)

1. **Task 11**: Use authenticated users for email notifications
2. **Task 12**: Include JWT validation in Docker container
3. **Task 13**: Add CI/CD tests for auth endpoints
4. **Task 14**: Use environment variables for secrets in cloud
5. **Task 15**: Frontend implementation with token management

---

**Task 10 Status**: ✅ **COMPLETE**
- Core authentication implemented
- JWT tokens working
- User registration functional
- Role-based access control active
- Backward compatible with existing public endpoints
