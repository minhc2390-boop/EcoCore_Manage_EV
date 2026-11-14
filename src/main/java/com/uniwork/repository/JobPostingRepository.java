package com.uniwork.repository;

import com.uniwork.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
