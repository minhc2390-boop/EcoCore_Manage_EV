package com.uniwork.repository;

import com.uniwork.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Quản lý Tin tuyển dụng
 * @author Hùng Anh - Company Admin Portal (Phần Tin Tuyển Dụng)
 */
@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    List<JobPosting> findByStatusOrderByCreatedAtDesc(String status);

    Page<JobPosting> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    List<JobPosting> findByCategoryIdAndStatus(Long categoryId, String status);

    List<JobPosting> findByJobTypeAndStatus(String jobType, String status);

    @Query("SELECT j FROM JobPosting j WHERE j.status = 'ACTIVE' AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(j.requirements) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<JobPosting> searchActiveJobs(@Param("keyword") String keyword);

    @Query(value = "SELECT j FROM JobPosting j WHERE j.status = 'ACTIVE' " +
           "AND (:keyword IS NULL OR :keyword = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR j.category.id = :categoryId) " +
           "AND (:jobType IS NULL OR :jobType = '' OR j.jobType LIKE CONCAT('%', :jobType, '%')) " +
           "ORDER BY j.createdAt DESC",
           countQuery = "SELECT count(j) FROM JobPosting j WHERE j.status = 'ACTIVE' " +
           "AND (:keyword IS NULL OR :keyword = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR j.category.id = :categoryId) " +
           "AND (:jobType IS NULL OR :jobType = '' OR j.jobType LIKE CONCAT('%', :jobType, '%'))")
    Page<JobPosting> searchJobsPaged(@Param("keyword") String keyword,
                                     @Param("categoryId") Long categoryId,
                                     @Param("jobType") String jobType,
                                     Pageable pageable);

    @Query(value = "SELECT j FROM JobPosting j WHERE j.status = 'ACTIVE' " +
           "AND (:keyword IS NULL OR :keyword = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR j.category.id = :categoryId) " +
           "AND (:jobType IS NULL OR :jobType = '' OR j.jobType LIKE CONCAT('%', :jobType, '%')) " +
           "AND (:level IS NULL OR :level = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :level, '%')) OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :level, '%'))) " +
           "AND (:experience IS NULL OR :experience = '' OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :experience, '%'))) " +
           "AND (:salary IS NULL OR :salary = '' OR LOWER(j.salaryRange) LIKE LOWER(CONCAT('%', :salary, '%'))) " +
           "AND (:education IS NULL OR :education = '' OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :education, '%'))) " +
           "AND (:postedAfter IS NULL OR j.createdAt >= :postedAfter) " +
           "ORDER BY j.createdAt DESC",
           countQuery = "SELECT count(j) FROM JobPosting j WHERE j.status = 'ACTIVE' " +
           "AND (:keyword IS NULL OR :keyword = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR j.category.id = :categoryId) " +
           "AND (:jobType IS NULL OR :jobType = '' OR j.jobType LIKE CONCAT('%', :jobType, '%')) " +
           "AND (:level IS NULL OR :level = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :level, '%')) OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :level, '%'))) " +
           "AND (:experience IS NULL OR :experience = '' OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :experience, '%'))) " +
           "AND (:salary IS NULL OR :salary = '' OR LOWER(j.salaryRange) LIKE LOWER(CONCAT('%', :salary, '%'))) " +
           "AND (:education IS NULL OR :education = '' OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :education, '%'))) " +
           "AND (:postedAfter IS NULL OR j.createdAt >= :postedAfter)")
    Page<JobPosting> searchJobsPagedAdvance(@Param("keyword") String keyword,
                                            @Param("categoryId") Long categoryId,
                                            @Param("jobType") String jobType,
                                            @Param("level") String level,
                                            @Param("experience") String experience,
                                            @Param("salary") String salary,
                                            @Param("education") String education,
                                            @Param("postedAfter") java.time.LocalDateTime postedAfter,
                                            Pageable pageable);

    List<JobPosting> findTop6ByStatusOrderByCreatedAtDesc(String status);

    List<JobPosting> findByEmployerId(Long employerId);

    // =========================================================================
    // CÁC HÀM TRUY VẤN DÀNH CHO COMPANY ADMIN (HÙNG ANH)
    // =========================================================================

    List<JobPosting> findByEmployerIdOrderByCreatedAtDesc(Long employerId);

    List<JobPosting> findByEmployerIdAndStatusOrderByCreatedAtDesc(Long employerId, String status);

    Optional<JobPosting> findByIdAndEmployerId(Long id, Long employerId);

    long countByEmployerId(Long employerId);

    long countByEmployerIdAndStatus(Long employerId, String status);

    @Query("SELECT j FROM JobPosting j WHERE j.employer.id = :employerId AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(j.shiftSchedule) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<JobPosting> searchEmployerJobs(@Param("employerId") Long employerId, @Param("keyword") String keyword);
}
