-- Flyway Migration V1: Initial Database Schema
-- Creates the core tables for the Resume Analyzer application
-- Author: AI Resume Analyzer Team
-- Date: 2026

-- ============================================================================
-- USERS TABLE
-- ============================================================================
-- Stores user account information
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);

-- ============================================================================
-- RESUMES TABLE
-- ============================================================================
-- Stores user-uploaded resumes
CREATE TABLE resumes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resumes_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create index on user_id for faster lookups
CREATE INDEX idx_resumes_user_id ON resumes(user_id);

-- ============================================================================
-- ANALYSES TABLE
-- ============================================================================
-- Stores resume analysis results
CREATE TABLE analyses (
    id BIGSERIAL PRIMARY KEY,
    resume_id BIGINT NOT NULL,
    job_description TEXT,
    match_percentage DOUBLE PRECISION NOT NULL,
    matched_skills TEXT,
    missing_skills TEXT,
    suggestions TEXT,
    ai_suggestions TEXT,
    report TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_analyses_resume_id FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

-- Create indexes for common queries
CREATE INDEX idx_analyses_resume_id ON analyses(resume_id);
CREATE INDEX idx_analyses_created_at ON analyses(created_at);

-- ============================================================================
-- COMMENTS (Documentation)
-- ============================================================================
-- Note: COMMENT ON syntax is PostgreSQL-specific, kept for documentation
-- H2 will skip these statements
-- COMMENT ON TABLE users IS 'Stores user account information for the Resume Analyzer application';
-- COMMENT ON TABLE resumes IS 'Stores uploaded resumes belonging to users';
-- COMMENT ON TABLE analyses IS 'Stores analysis results for resumes matched against job descriptions';
-- 
-- COMMENT ON COLUMN users.email IS 'Unique email address used for authentication';
-- COMMENT ON COLUMN users.password IS 'Hashed password for security';
-- COMMENT ON COLUMN resumes.content IS 'Full text content of the resume';
-- COMMENT ON COLUMN analyses.matched_skills IS 'JSON array of skills matched between resume and job';
-- COMMENT ON COLUMN analyses.missing_skills IS 'JSON array of skills missing from resume';
-- COMMENT ON COLUMN analyses.suggestions IS 'JSON array of improvement suggestions';
-- COMMENT ON COLUMN analyses.ai_suggestions IS 'JSON array of AI-generated suggestions';
