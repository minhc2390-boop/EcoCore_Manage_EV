package com.uniwork.service;

import com.uniwork.dto.JobSearchDTO;
import com.uniwork.entity.JobPosting;

import java.util.List;

public interface JobPostingService {
    List<JobPosting> getAllActiveJobs();
    List<JobPosting> getLatestJobs();
    JobPosting getJobById(Long id);
    List<JobPosting> searchJobs(JobSearchDTO searchDTO);
    List<JobPosting> getJobsByCategory(Long categoryId);
    List<JobPosting> getJobsByType(String jobType);
    JobPosting saveJob(JobPosting jobPosting);
    void deleteJob(Long id);
    void incrementViewCount(Long id);
}
