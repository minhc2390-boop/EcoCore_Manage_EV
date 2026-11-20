package com.uniwork.repository;

import com.uniwork.entity.JobPosting;
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

    List<JobPosting> findByCategoryIdAndStatus(Long categoryId, String status);

    List<JobPosting> findByJobTypeAndStatus(String jobType, String status);

    @Query("SELECT j FROM JobPosting j WHERE j.status = 'ACTIVE' AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(j.requirements) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<JobPosting> searchActiveJobs(@Param("keyword") String keyword);

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
