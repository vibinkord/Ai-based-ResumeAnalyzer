-- Flyway Migration V2: Add Performance Optimization Indexes
-- Adds additional indexes for common query patterns
-- Author: AI Resume Analyzer Team

-- ============================================================================
-- PERFORMANCE OPTIMIZATIONS
-- ============================================================================

-- Index for finding resumes by user and creation date (common in list queries)
CREATE INDEX idx_resumes_user_created ON resumes(user_id, created_at DESC);

-- Index for finding recent analyses (common in dashboard queries)
CREATE INDEX idx_analyses_created_desc ON analyses(created_at DESC);

-- Index for finding analyses with high match percentage
CREATE INDEX idx_analyses_match_percentage ON analyses(match_percentage DESC);

-- Index for user email case-insensitive searches
CREATE INDEX idx_users_email_lower ON users(LOWER(email));

-- ============================================================================
-- VACUUM ANALYZE (Performance maintenance)
-- ============================================================================
-- Note: This helps optimize query performance after initial data load
-- In production, this is typically run by maintenance jobs

VACUUM ANALYZE users;
VACUUM ANALYZE resumes;
VACUUM ANALYZE analyses;
