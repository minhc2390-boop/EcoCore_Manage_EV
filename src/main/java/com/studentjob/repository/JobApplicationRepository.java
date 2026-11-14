package com.studentjob.repository;

import com.studentjob.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByStudentIdOrderByAppliedAtDesc(Long studentId);
    List<JobApplication> findByJobPostingIdOrderByAppliedAtDesc(Long jobPostingId);
    boolean existsByStudentIdAndJobPostingId(Long studentId, Long jobPostingId);
    Optional<JobApplication> findByStudentIdAndJobPostingId(Long studentId, Long jobPostingId);
}
