package com.resumeanalyzer.repository;

import com.resumeanalyzer.model.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    /**
     * Find all resumes for a specific user
     *
     * @param userId the user's ID
     * @return list of resumes
     */
    List<Resume> findByUserId(Long userId);

    /**
     * Find all resumes for a specific user with pagination
     *
     * @param userId the user's ID
     * @param pageable pagination information
     * @return paginated list of resumes
     */
    Page<Resume> findByUserId(Long userId, Pageable pageable);

    /**
     * Find resumes by user ID, ordered by creation date (newest first)
     *
     * @param userId the user's ID
     * @return list of resumes sorted by creation date
     */
    @Query("SELECT r FROM Resume r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Resume> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    /**
     * Find resumes by user ID with pagination, ordered by creation date
     *
     * @param userId the user's ID
     * @param pageable pagination information
     * @return paginated list of resumes sorted by creation date
     */
    @Query("SELECT r FROM Resume r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    Page<Resume> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    /**
     * Count resumes for a specific user
     *
     * @param userId the user's ID
     * @return number of resumes
     */
    long countByUserId(Long userId);

    /**
     * Find resume by ID with user details
     *
     * @param id the resume ID
     * @return Optional containing the resume
     */
    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.user WHERE r.id = :id")
    Optional<Resume> findByIdWithUser(@Param("id") Long id);

    /**
     * Find all resumes with pagination
     *
     * @param pageable pagination information
     * @return paginated list of all resumes
     */
    @Override
    Page<Resume> findAll(Pageable pageable);
}
