package com.uniwork.service;

import com.uniwork.dto.JobApplicationDTO;
import com.uniwork.entity.JobApplication;

import java.util.List;

/**
 * Service Quản lý Đơn Ứng Tuyển
 * @author VY - Company Admin Portal (Phần Tuyển dụng & Quản lý ứng viên)
 */
public interface JobApplicationService {
    // Sinh viên nộp đơn
    JobApplication applyForJob(JobApplicationDTO dto);

    // Lấy danh sách đơn của sinh viên
    List<JobApplication> getApplicationsByStudent(Long studentId);

    // Lấy danh sách đơn theo bài đăng
    List<JobApplication> getApplicationsByJob(Long jobId);

    // Kiểm tra đã nộp chưa
    boolean hasApplied(Long studentId, Long jobId);

    /**
     * Lấy tất cả đơn ứng tuyển vào công ty của Company Admin (tất cả trạng thái)
     * @author VY
     */
    List<JobApplication> getApplicationsByEmployer(Long employerId);

    /**
     * Lấy đơn ứng tuyển vào công ty theo trạng thái
     * @author VY
     */
    List<JobApplication> getApplicationsByEmployerAndStatus(Long employerId, String status);

    /**
     * Company Admin cập nhật trạng thái đơn ứng tuyển:
     * PENDING -> INTERVIEW (Mời phỏng vấn)
     * PENDING / INTERVIEW -> ACCEPTED (Nhận việc)
     * PENDING / INTERVIEW / REVIEWING -> REJECTED (Từ chối)
     * @author VY
     */
    JobApplication updateApplicationStatus(Long applicationId, String newStatus, Long employerId);

    /**
     * Lấy đơn ứng tuyển theo id (cho Company Admin xem)
     * @author VY
     */
    JobApplication getApplicationById(Long applicationId);

    /**
     * Đếm tổng đơn ứng tuyển của công ty
     * @author VY
     */
    long countApplicationsByEmployer(Long employerId);

    /**
     * Đếm đơn theo trạng thái của công ty
     * @author VY
     */
    long countApplicationsByEmployerAndStatus(Long employerId, String status);
}
