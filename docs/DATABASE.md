# Database Architecture & Configuration

## Overview

The AI Resume Analyzer uses a relational database to persist user data, resumes, and analysis results. The application supports both **PostgreSQL** (production) and **H2** (testing) databases.

### Key Features:
- **Spring Data JPA** for ORM (Object-Relational Mapping)
- **Hibernate** as the JPA implementation
- **Flyway** for database migrations and versioning
- **Automatic Auditing** with timestamp tracking (createdAt, updatedAt)
- **Cascade Operations** for maintaining data integrity
- **Performance Optimization** with strategic indexing

---

## Database Schema

### Entity-Relationship Diagram

```
┌─────────────┐
│    USER     │
├─────────────┤
│ id (PK)     │
│ email       │ ◄────── UNIQUE INDEX
│ password    │
│ fullName    │
│ createdAt   │
│ updatedAt   │
└─────────────┘
       │
       │ 1:N (orphanRemoval=true)
       │
       ▼
┌──────────────┐
│   RESUME     │
├──────────────┤
│ id (PK)      │
│ user_id (FK) │ ◄────── INDEXED
│ filename     │
│ content      │
│ createdAt    │
│ updatedAt    │
└──────────────┘
       │
       │ 1:N (orphanRemoval=true)
       │
       ▼
┌───────────────┐
│   ANALYSIS    │
├───────────────┤
│ id (PK)       │
│ resume_id(FK) │ ◄────── INDEXED
│ jobDescription│
│ matchPercent  │
│ matchedSkills │
│ missingSkills │
│ suggestions   │
│ aiSuggestions │
│ report        │
│ createdAt     │ ◄────── INDEXED
│ updatedAt     │
└───────────────┘
```

### Table Details

#### 1. **USERS Table**

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| email | VARCHAR(255) | UNIQUE, NOT NULL | Login credentials |
| password | VARCHAR(255) | NOT NULL | Bcrypt hashed |
| full_name | VARCHAR(255) | NOT NULL | User's full name |
| created_at | TIMESTAMP | NOT NULL | Auto-set by Hibernate |
| updated_at | TIMESTAMP | NOT NULL | Auto-updated by Hibernate |

**Indexes:**
- `idx_users_email`: UNIQUE index on email for fast lookups
- `idx_users_email_lower`: Index on LOWER(email) for case-insensitive searches

#### 2. **RESUMES Table**

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| user_id | BIGINT | FOREIGN KEY (USERS.id), NOT NULL, ON DELETE CASCADE | User who owns this resume |
| filename | VARCHAR(255) | NOT NULL | Original filename (e.g., "resume.pdf") |
| content | TEXT | NOT NULL | Extracted text content from resume |
| created_at | TIMESTAMP | NOT NULL | Auto-set by Hibernate |
| updated_at | TIMESTAMP | NOT NULL | Auto-updated by Hibernate |

**Indexes:**
- `idx_resumes_user_id`: Index on user_id for fast user lookups
- `idx_resumes_user_created`: Composite index on (user_id, created_at DESC) for sorting

#### 3. **ANALYSES Table**

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| resume_id | BIGINT | FOREIGN KEY (RESUMES.id), NOT NULL, ON DELETE CASCADE | Resume being analyzed |
| job_description | TEXT | NULL | Job posting text provided by user |
| match_percentage | DOUBLE PRECISION | NOT NULL | Percentage match (0-100) |
| matched_skills | TEXT | NULL | JSON array of matched skills |
| missing_skills | TEXT | NULL | JSON array of missing skills |
| suggestions | TEXT | NULL | JSON array of suggestions |
| ai_suggestions | TEXT | NULL | JSON array of AI-generated suggestions |
| report | TEXT | NULL | Full analysis report |
| created_at | TIMESTAMP | NOT NULL | Auto-set by Hibernate |
| updated_at | TIMESTAMP | NOT NULL | Auto-updated by Hibernate |

**Indexes:**
- `idx_analyses_resume_id`: Index on resume_id for fast analysis lookups
- `idx_analyses_created_at`: Index on created_at DESC for recent analyses
- `idx_analyses_match_percentage`: Index on match_percentage DESC for sorting by score

---

## Entity Classes

### User Entity

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;  // Bcrypt hashed
    
    @Column(nullable = false)
    private String fullName;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // One user can have many resumes
    // orphanRemoval ensures resumes are deleted when removed from this collection
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, 
               orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Resume> resumes = new ArrayList<>();
}
```

### Resume Entity

```java
@Entity
@Table(name = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // One resume can have many analyses
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, 
               orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Analysis> analyses = new ArrayList<>();
    
    public int getAnalysisCount() {
        return analyses.size();
    }
}
```

### Analysis Entity

```java
@Entity
@Table(name = "analyses")
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;
    
    @Column(columnDefinition = "TEXT")
    private String jobDescription;
    
    @Column(nullable = false)
    private Double matchPercentage;
    
    @Column(columnDefinition = "TEXT")
    private String matchedSkills;        // JSON array
    
    @Column(columnDefinition = "TEXT")
    private String missingSkills;        // JSON array
    
    @Column(columnDefinition = "TEXT")
    private String suggestions;          // JSON array
    
    @Column(columnDefinition = "TEXT")
    private String aiSuggestions;        // JSON array
    
    @Column(columnDefinition = "TEXT")
    private String report;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public String getFormattedMatchPercentage() {
        return String.format("%.1f%%", matchPercentage);
    }
    
    public boolean isGoodMatch() {
        return matchPercentage >= 70.0;
    }
    
    public boolean isAcceptableMatch() {
        return matchPercentage >= 50.0 && matchPercentage < 70.0;
    }
    
    public boolean isPoorMatch() {
        return matchPercentage < 50.0;
    }
}
```

---

## Database Configuration

### Production Configuration (PostgreSQL)

**File:** `src/main/resources/application.properties`

```properties
# PostgreSQL Database
spring.datasource.url=jdbc:postgresql://localhost:5432/resume_analyzer
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true

# Flyway Migrations
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```

### Development Configuration (PostgreSQL)

**File:** `src/main/resources/application-dev.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/resume_analyzer_dev
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

### Test Configuration (H2 In-Memory)

**File:** `src/main/resources/application-test.properties`

```properties
# H2 In-Memory Database (auto-cleanup after test)
spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver

# Hibernate creates schema from entities
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

# No Flyway for tests (H2 uses Hibernate DDL)
spring.flyway.enabled=false
spring.sql.init.mode=never
```

---

## Database Migrations (Flyway)

Flyway automatically manages schema evolution through versioned SQL scripts.

### V1__Initial_Schema.sql
Creates core tables: users, resumes, analyses

### V2__Add_Performance_Indexes.sql
Adds composite and performance-tuning indexes:
- Composite index on resumes(user_id, created_at DESC)
- Index on analyses(match_percentage DESC)
- Case-insensitive index on users email

**To apply migrations manually:**
```bash
mvn flyway:migrate
```

**To baseline existing database:**
```bash
mvn flyway:baseline
```

---

## Repository Layer

### UserRepository

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByEmailIgnoreCase(@Param("email") String email);
}
```

### ResumeRepository

```java
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);
    List<Resume> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserId(Long userId);
    Optional<Resume> findByIdWithUser(Long id); // Custom fetch with user
}
```

### AnalysisRepository

```java
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    List<Analysis> findByResumeId(Long resumeId);
    List<Analysis> findByResumeIdOrderByCreatedAtDesc(Long resumeId);
    long countByResumeId(Long resumeId);
    List<Analysis> findGoodMatches(); // matchPercentage >= 70
    List<Analysis> findByDateRange(LocalDateTime start, LocalDateTime end);
    List<Analysis> findByUserId(Long userId);
    Double getAverageMatchPercentage();
    Double getAverageMatchPercentageForUser(Long userId);
}
```

---

## Common Operations

### Create a User

```java
User user = new User("john@example.com", "hashedPassword", "John Doe");
userRepository.save(user);
```

### Upload a Resume

```java
User user = userRepository.findById(userId).orElseThrow();
Resume resume = new Resume(user, "resume.pdf", extractedText);
resumeRepository.save(resume);
```

### Create an Analysis

```java
Resume resume = resumeRepository.findById(resumeId).orElseThrow();
Analysis analysis = new Analysis(resume, jobDescription, 85.5);
analysis.setMatchedSkills(jsonArrayAsString);
analysis.setMissingSkills(jsonArrayAsString);
analysisRepository.save(analysis);
```

### Find User's Resumes (Most Recent First)

```java
List<Resume> resumes = resumeRepository
    .findByUserIdOrderByCreatedAtDesc(userId);
```

### Find Analyses for a Resume

```java
List<Analysis> analyses = analysisRepository
    .findByResumeIdOrderByCreatedAtDesc(resumeId);
```

### Find Good Matches (70%+ score)

```java
List<Analysis> goodMatches = analysisRepository.findGoodMatches();
```

---

## Performance Optimization

### 1. **Lazy Loading Strategy**
- Relationships use `fetch = FetchType.LAZY` to avoid N+1 queries
- Load relationships explicitly when needed using custom repository queries

### 2. **Indexed Columns**
- Email (unique index) for fast user lookup
- user_id in resumes for filtering by user
- resume_id in analyses for filtering by resume
- Composite indexes for common sort patterns

### 3. **Batch Processing**
```properties
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

### 4. **Query Optimization**
- Use `@Query` with FETCH JOIN to load relationships in one query
- Use repository count methods instead of loading full collections

### 5. **Caching (Optional)**
```java
@Cacheable("users")
Optional<User> findByEmail(String email);
```

---

## Transaction Management

All database operations are automatically transactional via `@Transactional` on service methods:

```java
@Service
@Transactional
public class AnalysisService {
    public Analysis saveAnalysis(Analysis analysis) {
        // Automatically wrapped in transaction
        return analysisRepository.save(analysis);
    }
}
```

**Transaction Isolation Level:** READ_COMMITTED (default)
**Propagation:** REQUIRED (default)

---

## Data Integrity

### Cascade Behavior
- **User → Resume**: `CascadeType.ALL + orphanRemoval=true`
  - Deleting a user deletes all associated resumes
  - Removing a resume from user's collection deletes it

- **Resume → Analysis**: `CascadeType.ALL + orphanRemoval=true`
  - Deleting a resume deletes all associated analyses
  - Removing an analysis from resume's collection deletes it

### Foreign Key Constraints
- Resume.user_id → User.id (ON DELETE CASCADE)
- Analysis.resume_id → Resume.id (ON DELETE CASCADE)

### Unique Constraints
- User.email must be unique
- Foreign keys ensure referential integrity

---

## Backup & Recovery

### PostgreSQL Backup

```bash
# Full backup
pg_dump -U postgres -h localhost resume_analyzer > backup.sql

# Restore
psql -U postgres -h localhost resume_analyzer < backup.sql

# Backup specific table
pg_dump -U postgres -h localhost -t users resume_analyzer > users_backup.sql
```

### H2 Database

H2 in-memory test database is automatically recreated for each test run.

For H2 file-based mode:
```properties
spring.datasource.url=jdbc:h2:file:./data/test
```

---

## Monitoring & Debugging

### Enable SQL Logging

```properties
# Show all generated SQL
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging level
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Check Database Connections

```properties
# HikariCP connection pool logging
logging.level.com.zaxxer.hikari=DEBUG
```

### Flyway Validation

```bash
# Check migration status
mvn flyway:info
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| "Table not found" | Run migrations: `mvn flyway:migrate` |
| Foreign key violations | Check cascade settings and orphanRemoval configuration |
| Lazy loading errors | Load relationships before closing session or use JOIN FETCH queries |
| Duplicate key error | Ensure unique constraints are applied correctly |
| Connection pool exhausted | Increase `maximum-pool-size` in HikariCP config |

---

## Best Practices

1. **Always use DTOs** for API responses to avoid lazy loading issues
2. **Use @Transactional** at service layer, not controller
3. **Fetch eagerly** in repository queries for relationships you'll use
4. **Use batch operations** for bulk inserts/updates
5. **Index foreign keys** and frequently searched columns
6. **Monitor query performance** using slow query logs
7. **Use database-level constraints** alongside JPA validations
8. **Version all migrations** with Flyway
9. **Test with H2** in-memory, deploy with PostgreSQL
10. **Connection pooling** essential for production performance

