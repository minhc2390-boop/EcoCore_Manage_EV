package com.uniwork.service.impl;

import com.uniwork.dto.JobApplicationDTO;
import com.uniwork.entity.JobApplication;
import com.uniwork.entity.JobPosting;
import com.uniwork.entity.User;
import com.uniwork.exception.ResourceNotFoundException;
import com.uniwork.repository.JobApplicationRepository;
import com.uniwork.repository.JobPostingRepository;
import com.uniwork.repository.UserRepository;
import com.uniwork.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Triển khai nghiệp vụ Quản lý Đơn Ứng Tuyển
 * @author VY - Company Admin Portal (Phần Tuyển dụng & Quản lý ứng viên)
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    // =========================================================================
    // SINH VIÊN NỘP ĐƠN ỨNG TUYỂN
    // =========================================================================

    @Override
    public JobApplication applyForJob(JobApplicationDTO dto) {
        if (jobApplicationRepository.existsByStudentIdAndJobPostingId(dto.getStudentId(), dto.getJobPostingId())) {
            throw new IllegalStateException("Bạn đã nộp hồ sơ ứng tuyển cho công việc này rồi!");
        }

        User student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin sinh viên"));

        JobPosting job = jobPostingRepository.findById(dto.getJobPostingId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tin tuyển dụng"));

        JobApplication application = JobApplication.builder()
                .student(student)
                .jobPosting(job)
                .cvUrl(dto.getCvUrl())
                .coverLetter(dto.getCoverLetter())
                .studentScheduleNotes(dto.getStudentScheduleNotes())
                .status("PENDING")
                .appliedAt(LocalDateTime.now())
                .build();

        JobApplication saved = jobApplicationRepository.save(application);
        log.info("Sinh viên '{}' đã nộp đơn ứng tuyển vào bài đăng ID: {}", student.getUsername(), job.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplication> getApplicationsByStudent(Long studentId) {
        return jobApplicationRepository.findByStudentIdOrderByAppliedAtDesc(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplication> getApplicationsByJob(Long jobId) {
        return jobApplicationRepository.findByJobPostingIdOrderByAppliedAtDesc(jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasApplied(Long studentId, Long jobId) {
        return jobApplicationRepository.existsByStudentIdAndJobPostingId(studentId, jobId);
    }

    // =========================================================================
    // COMPANY ADMIN QUẢN LÝ ĐƠN ỨNG TUYỂN (VY)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<JobApplication> getApplicationsByEmployer(Long employerId) {
        return jobApplicationRepository.findByEmployerIdOrderByAppliedAtDesc(employerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplication> getApplicationsByEmployerAndStatus(Long employerId, String status) {
        if (status == null || status.trim().isEmpty() || "ALL".equalsIgnoreCase(status)) {
            return getApplicationsByEmployer(employerId);
        }
        return jobApplicationRepository.findByEmployerIdAndStatusOrderByAppliedAtDesc(employerId, status.toUpperCase());
    }

    @Override
    public JobApplication updateApplicationStatus(Long applicationId, String newStatus, Long employerId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn ứng tuyển ID: " + applicationId));

        // Kiểm tra bảo mật: đơn này phải thuộc bài đăng của công ty mình
        if (!application.getJobPosting().getEmployer().getId().equals(employerId)) {
            throw new IllegalStateException("Bạn không có quyền cập nhật đơn ứng tuyển này!");
        }

        // Chỉ cho phép các trạng thái hợp lệ
        String status = newStatus.toUpperCase();
        if (!List.of("PENDING", "REVIEWING", "INTERVIEW", "ACCEPTED", "REJECTED").contains(status)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ: " + newStatus);
        }

        String oldStatus = application.getStatus();
        application.setStatus(status);
        JobApplication updated = jobApplicationRepository.save(application);
        log.info("Company Admin (Employer ID: {}) đã cập nhật trạng thái đơn ID: {} từ {} → {}",
                employerId, applicationId, oldStatus, status);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public JobApplication getApplicationById(Long applicationId) {
        return jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn ứng tuyển ID: " + applicationId));
    }

    @Override
    @Transactional(readOnly = true)
    public long countApplicationsByEmployer(Long employerId) {
        return jobApplicationRepository.countByEmployerId(employerId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countApplicationsByEmployerAndStatus(Long employerId, String status) {
        return jobApplicationRepository.countByEmployerIdAndStatus(employerId, status);
    }
}
