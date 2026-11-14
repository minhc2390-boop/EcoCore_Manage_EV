package com.studentjob.service.impl;

import com.studentjob.dto.JobSearchDTO;
import com.studentjob.entity.JobPosting;
import com.studentjob.exception.ResourceNotFoundException;
import com.studentjob.repository.JobPostingRepository;
import com.studentjob.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository jobPostingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> getAllActiveJobs() {
        return jobPostingRepository.findByStatusOrderByCreatedAtDesc("ACTIVE");
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> getLatestJobs() {
        return jobPostingRepository.findTop6ByStatusOrderByCreatedAtDesc("ACTIVE");
    }

    @Override
    @Transactional(readOnly = true)
    public JobPosting getJobById(Long id) {
        return jobPostingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin tuyển dụng với mã: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> searchJobs(JobSearchDTO searchDTO) {
        if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().trim().isEmpty()) {
            return jobPostingRepository.searchActiveJobs(searchDTO.getKeyword().trim());
        }
        if (searchDTO.getCategoryId() != null) {
            return jobPostingRepository.findByCategoryIdAndStatus(searchDTO.getCategoryId(), "ACTIVE");
        }
        if (searchDTO.getJobType() != null && !searchDTO.getJobType().trim().isEmpty()) {
            return jobPostingRepository.findByJobTypeAndStatus(searchDTO.getJobType().trim(), "ACTIVE");
        }
        return getAllActiveJobs();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> getJobsByCategory(Long categoryId) {
        return jobPostingRepository.findByCategoryIdAndStatus(categoryId, "ACTIVE");
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> getJobsByType(String jobType) {
        return jobPostingRepository.findByJobTypeAndStatus(jobType, "ACTIVE");
    }

    @Override
    public JobPosting saveJob(JobPosting jobPosting) {
        return jobPostingRepository.save(jobPosting);
    }

    @Override
    public void deleteJob(Long id) {
        JobPosting job = getJobById(id);
        job.setStatus("CLOSED");
        jobPostingRepository.save(job);
    }

    @Override
    public void incrementViewCount(Long id) {
        jobPostingRepository.findById(id).ifPresent(job -> {
            job.setViewCount(job.getViewCount() + 1);
            jobPostingRepository.save(job);
        });
    }
}
