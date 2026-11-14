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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

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

        return jobApplicationRepository.save(application);
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
}
