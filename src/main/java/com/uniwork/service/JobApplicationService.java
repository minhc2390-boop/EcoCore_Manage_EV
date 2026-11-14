package com.uniwork.service;

import com.uniwork.dto.JobApplicationDTO;
import com.uniwork.entity.JobApplication;

import java.util.List;

public interface JobApplicationService {
    JobApplication applyForJob(JobApplicationDTO dto);
    List<JobApplication> getApplicationsByStudent(Long studentId);
    List<JobApplication> getApplicationsByJob(Long jobId);
    boolean hasApplied(Long studentId, Long jobId);
}
