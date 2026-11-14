package com.studentjob.service;

import com.studentjob.dto.JobApplicationDTO;
import com.studentjob.entity.JobApplication;

import java.util.List;

public interface JobApplicationService {
    JobApplication applyForJob(JobApplicationDTO dto);
    List<JobApplication> getApplicationsByStudent(Long studentId);
    List<JobApplication> getApplicationsByJob(Long jobId);
    boolean hasApplied(Long studentId, Long jobId);
}
