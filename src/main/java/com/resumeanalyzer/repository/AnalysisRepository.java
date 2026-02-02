package com.resumeanalyzer.repository;

import com.resumeanalyzer.model.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    /**
     * Find all analyses for a specific resume
     *
     * @param resumeId the resume's ID
     * @return list of analyses
     */
    List<Analysis> findByResumeId(Long resumeId);

    /**
     * Find analyses by resume ID, ordered by creation date (newest first)
     *
     * @param resumeId the resume's ID
     * @return list of analyses sorted by creation date
     */
    @Query("SELECT a FROM Analysis a WHERE a.resume.id = :resumeId ORDER BY a.createdAt DESC")
    List<Analysis> findByResumeIdOrderByCreatedAtDesc(@Param("resumeId") Long resumeId);

    /**
     * Count analyses for a specific resume
     *
     * @param resumeId the resume's ID
     * @return number of analyses
     */
    long countByResumeId(Long resumeId);

    /**
     * Find high-scoring analyses (match >= 70%)
     *
     * @return list of analyses with good match percentage
     */
    @Query("SELECT a FROM Analysis a WHERE a.matchPercentage >= 70.0 ORDER BY a.matchPercentage DESC")
    List<Analysis> findGoodMatches();

    /**
     * Find analyses within a date range
     *
     * @param startDate start date
     * @param endDate   end date
     * @return list of analyses created between dates
     */
    @Query("SELECT a FROM Analysis a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<Analysis> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find analyses by user (through resume relationship)
     *
     * @param userId the user's ID
     * @return list of all analyses for a user
     */
    @Query("SELECT a FROM Analysis a WHERE a.resume.user.id = :userId ORDER BY a.createdAt DESC")
    List<Analysis> findByUserId(@Param("userId") Long userId);

    /**
     * Get average match percentage for all analyses
     *
     * @return average match percentage
     */
    @Query("SELECT AVG(a.matchPercentage) FROM Analysis a")
    Double getAverageMatchPercentage();

    /**
     * Get average match percentage for a specific user's analyses
     *
     * @param userId the user's ID
     * @return average match percentage
     */
    @Query("SELECT AVG(a.matchPercentage) FROM Analysis a WHERE a.resume.user.id = :userId")
    Double getAverageMatchPercentageForUser(@Param("userId") Long userId);
}
