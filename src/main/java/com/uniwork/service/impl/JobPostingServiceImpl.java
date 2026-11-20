package com.uniwork.service.impl;

import com.uniwork.dto.JobSearchDTO;
import com.uniwork.entity.JobPosting;
import com.uniwork.exception.ResourceNotFoundException;
import com.uniwork.repository.JobPostingRepository;
import com.uniwork.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Triển khai nghiệp vụ Tin tuyển dụng (JobPosting)
 * @author Hùng Anh - Company Admin Portal (Phần Tin Tuyển Dụng)
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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
    public Page<JobPosting> searchJobsPaged(JobSearchDTO searchDTO, Pageable pageable) {
        String keyword = (searchDTO != null && searchDTO.getKeyword() != null && !searchDTO.getKeyword().trim().isEmpty())
                ? searchDTO.getKeyword().trim() : null;
        Long categoryId = (searchDTO != null) ? searchDTO.getCategoryId() : null;
        String jobType = (searchDTO != null && searchDTO.getJobType() != null && !searchDTO.getJobType().trim().isEmpty())
                ? searchDTO.getJobType().trim() : null;

        return jobPostingRepository.searchJobsPaged(keyword, categoryId, jobType, pageable);
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

    // =========================================================================
    // NGHIỆP VỤ COMPANY ADMIN PORTAL (HÙNG ANH)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> getJobsByEmployerId(Long employerId) {
        return jobPostingRepository.findByEmployerIdOrderByCreatedAtDesc(employerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> getJobsByEmployerIdAndStatus(Long employerId, String status) {
        if (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status)) {
            return getJobsByEmployerId(employerId);
        }
        return jobPostingRepository.findByEmployerIdAndStatusOrderByCreatedAtDesc(employerId, status.toUpperCase());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> searchJobsByEmployer(Long employerId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getJobsByEmployerId(employerId);
        }
        return jobPostingRepository.searchEmployerJobs(employerId, keyword.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public JobPosting getJobByIdAndEmployerId(Long id, Long employerId) {
        return jobPostingRepository.findByIdAndEmployerId(id, employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin tuyển dụng hoặc bài đăng không thuộc công ty bạn quản lý!"));
    }

    @Override
    public JobPosting toggleJobStatus(Long id, Long employerId) {
        JobPosting job = getJobByIdAndEmployerId(id, employerId);
        if ("ACTIVE".equalsIgnoreCase(job.getStatus())) {
            job.setStatus("HIDDEN");
            log.info("Company Admin (Employer ID: {}) đã ẩn bài đăng ID: {}", employerId, id);
        } else {
            job.setStatus("ACTIVE");
            log.info("Company Admin (Employer ID: {}) đã kích hoạt hiển thị bài đăng ID: {}", employerId, id);
        }
        return jobPostingRepository.save(job);
    }

    @Override
    public JobPosting extendJobDeadline(Long id, Long employerId, int additionalDays) {
        JobPosting job = getJobByIdAndEmployerId(id, employerId);
        LocalDate baseDate = (job.getDeadline() != null && job.getDeadline().isAfter(LocalDate.now()))
                ? job.getDeadline()
                : LocalDate.now();
        LocalDate newDeadline = baseDate.plusDays(additionalDays);
        job.setDeadline(newDeadline);
        if ("EXPIRED".equalsIgnoreCase(job.getStatus()) || "CLOSED".equalsIgnoreCase(job.getStatus())) {
            job.setStatus("ACTIVE");
        }
        log.info("Company Admin (Employer ID: {}) đã gia hạn bài đăng ID: {} thêm {} ngày (hạn mới: {})",
                employerId, id, additionalDays, newDeadline);
        return jobPostingRepository.save(job);
    }

    @Override
    public JobPosting extendJobDeadline(Long id, Long employerId, LocalDate newDeadline) {
        JobPosting job = getJobByIdAndEmployerId(id, employerId);
        job.setDeadline(newDeadline);
        if ("EXPIRED".equalsIgnoreCase(job.getStatus()) || "CLOSED".equalsIgnoreCase(job.getStatus())) {
            job.setStatus("ACTIVE");
        }
        log.info("Company Admin (Employer ID: {}) đã gia hạn bài đăng ID: {} đến ngày: {}",
                employerId, id, newDeadline);
        return jobPostingRepository.save(job);
    }

    @Override
    public void deleteJobByEmployer(Long id, Long employerId) {
        JobPosting job = getJobByIdAndEmployerId(id, employerId);
        job.setStatus("CLOSED");
        jobPostingRepository.save(job);
        log.info("Company Admin (Employer ID: {}) đã đóng/xóa bài đăng ID: {}", employerId, id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countJobsByEmployer(Long employerId) {
        return jobPostingRepository.countByEmployerId(employerId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countJobsByEmployerAndStatus(Long employerId, String status) {
        return jobPostingRepository.countByEmployerIdAndStatus(employerId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalViewsByEmployer(Long employerId) {
        return jobPostingRepository.findByEmployerId(employerId).stream()
                .mapToLong(j -> j.getViewCount() != null ? j.getViewCount() : 0L)
                .sum();
    }
}
