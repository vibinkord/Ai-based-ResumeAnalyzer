package com.resumeanalyzer.repository.search;

import com.resumeanalyzer.model.document.ResumeDocument;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Elasticsearch Repository for Resume Full-Text Search
 * 
 * Provides full-text search capabilities for resumes with:
 * - Query DSL support
 * - Aggregation support
 * - Pagination support
 * - Real-time indexing
 * 
 * Search Methods:
 * - Search by content (full-text)
 * - Filter by skills (exact match)
 * - Filter by domain/specialization
 * - Find by user ID with pagination
 * - Advanced boolean queries
 * 
 * @author Resume Analyzer Team
 * @version 1.0.0
 */
@Repository
@ConditionalOnProperty(
    name = "elasticsearch.enabled",
    havingValue = "true",
    matchIfMissing = false
)
public interface ResumeSearchRepository extends ElasticsearchRepository<ResumeDocument, String> {

    /**
     * Find all resumes for a user with pagination
     * 
     * @param userId User ID
     * @param pageable Pagination information
     * @return Paginated list of resume documents
     */
    Page<ResumeDocument> findByUserId(String userId, Pageable pageable);

    /**
     * Find resumes by skill
     * 
     * @param skill Skill to search for
     * @param pageable Pagination information
     * @return Paginated list of matching resumes
     */
    Page<ResumeDocument> findBySkillsContains(String skill, Pageable pageable);

    /**
     * Find resumes by domain/specialization
     * 
     * @param domain Domain to search for
     * @param pageable Pagination information
     * @return Paginated list of resumes in domain
     */
    Page<ResumeDocument> findByDomain(String domain, Pageable pageable);

    /**
     * Find resumes by programming language
     * 
     * @param language Programming language
     * @param pageable Pagination information
     * @return Paginated list of matching resumes
     */
    Page<ResumeDocument> findByProgrammingLanguagesContains(String language, Pageable pageable);

    /**
     * Find active resumes for a user
     * 
     * @param userId User ID
     * @param isActive Active status
     * @param pageable Pagination information
     * @return Paginated list of active resumes
     */
    Page<ResumeDocument> findByUserIdAndIsActive(String userId, Boolean isActive, Pageable pageable);

    /**
     * Find indexed resumes
     * 
     * @param isIndexed Indexing status
     * @param pageable Pagination information
     * @return Paginated list of indexed resumes
     */
    Page<ResumeDocument> findByIsIndexed(Boolean isIndexed, Pageable pageable);

    /**
     * Find resumes with minimum quality score
     * 
     * @param minScore Minimum quality score
     * @param pageable Pagination information
     * @return Paginated list of resumes with good quality
     */
    Page<ResumeDocument> findByQualityScoreGreaterThanEqual(Integer minScore, Pageable pageable);

    /**
     * Find resumes by location
     * 
     * @param location Location/city
     * @param pageable Pagination information
     * @return Paginated list of resumes from location
     */
    Page<ResumeDocument> findByLocation(String location, Pageable pageable);

    /**
     * Find all resumes by active status
     * 
     * @param isActive Active status
     * @return List of active/inactive resumes
     */
    List<ResumeDocument> findByIsActive(Boolean isActive);

    /**
     * Count resumes by domain
     * 
     * @param domain Domain/specialization
     * @return Number of resumes in domain
     */
    long countByDomain(String domain);

    /**
     * Count active resumes for user
     * 
     * @param userId User ID
     * @param isActive Active status
     * @return Number of active resumes
     */
    long countByUserIdAndIsActive(String userId, Boolean isActive);
}
