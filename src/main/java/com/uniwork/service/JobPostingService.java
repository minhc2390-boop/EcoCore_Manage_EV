package com.uniwork.service;

import com.uniwork.dto.JobSearchDTO;
import com.uniwork.entity.JobPosting;

import java.time.LocalDate;
import java.util.List;

/**
 * Service Quản Lý Tin Tuyển Dụng
 * @author Hùng Anh - Company Admin Portal (Phần Tin Tuyển Dụng)
 */
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

    // =========================================================================
    // CÁC HÀM NGHIỆP VỤ DÀNH RIÊNG CHO COMPANY ADMIN (HÙNG ANH)
    // =========================================================================

    /**
     * Lấy danh sách tin tuyển dụng thuộc riêng công ty
     */
    List<JobPosting> getJobsByEmployerId(Long employerId);

    /**
     * Lấy danh sách tin tuyển dụng của công ty theo trạng thái (ACTIVE, HIDDEN...)
     */
    List<JobPosting> getJobsByEmployerIdAndStatus(Long employerId, String status);

    /**
     * Tìm kiếm tin tuyển dụng trong nội bộ công ty
     */
    List<JobPosting> searchJobsByEmployer(Long employerId, String keyword);

    /**
     * Lấy chi tiết bài đăng và kiểm tra quyền sở hữu của công ty
     */
    JobPosting getJobByIdAndEmployerId(Long id, Long employerId);

    /**
     * Ẩn tin hoặc kích hoạt lại tin tuyển dụng của công ty
     */
    JobPosting toggleJobStatus(Long id, Long employerId);

    /**
     * Gia hạn tin tuyển dụng thêm số ngày
     */
    JobPosting extendJobDeadline(Long id, Long employerId, int additionalDays);

    /**
     * Gia hạn tin tuyển dụng đến một ngày cụ thể
     */
    JobPosting extendJobDeadline(Long id, Long employerId, LocalDate newDeadline);

    /**
     * Xóa / Đóng bài tuyển dụng của công ty
     */
    void deleteJobByEmployer(Long id, Long employerId);

    /**
     * Tổng số bài đăng của công ty
     */
    long countJobsByEmployer(Long employerId);

    /**
     * Số bài đăng theo trạng thái của công ty
     */
    long countJobsByEmployerAndStatus(Long employerId, String status);

    /**
     * Tổng lượt xem tất cả các bài đăng của công ty
     */
    long getTotalViewsByEmployer(Long employerId);
}
