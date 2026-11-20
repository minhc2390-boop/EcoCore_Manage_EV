package com.uniwork.repository;

import com.uniwork.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Quản lý Đơn Ứng Tuyển của Sinh Viên
 * @author VY - Company Admin Portal (Phần Tuyển dụng & Quản lý ứng viên)
 */
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    // Tìm đơn ứng tuyển của 1 sinh viên
    List<JobApplication> findByStudentIdOrderByAppliedAtDesc(Long studentId);

    // Tìm đơn ứng tuyển cho 1 bài đăng cụ thể
    List<JobApplication> findByJobPostingIdOrderByAppliedAtDesc(Long jobPostingId);

    // Kiểm tra sinh viên đã nộp chưa
    boolean existsByStudentIdAndJobPostingId(Long studentId, Long jobPostingId);

    // Lấy 1 đơn cụ thể theo sinh viên + bài đăng
    Optional<JobApplication> findByStudentIdAndJobPostingId(Long studentId, Long jobPostingId);

    /**
     * Lấy tất cả đơn ứng tuyển vào công ty của Company Admin
     * (join qua jobPosting -> employer)
     * @author VY
     */
    @Query("SELECT a FROM JobApplication a WHERE a.jobPosting.employer.id = :employerId ORDER BY a.appliedAt DESC")
    List<JobApplication> findByEmployerIdOrderByAppliedAtDesc(@Param("employerId") Long employerId);

    /**
     * Lấy đơn ứng tuyển vào công ty theo trạng thái
     * @author VY
     */
    @Query("SELECT a FROM JobApplication a WHERE a.jobPosting.employer.id = :employerId AND a.status = :status ORDER BY a.appliedAt DESC")
    List<JobApplication> findByEmployerIdAndStatusOrderByAppliedAtDesc(@Param("employerId") Long employerId, @Param("status") String status);

    /**
     * Đếm tổng số đơn ứng tuyển vào công ty
     * @author VY
     */
    @Query("SELECT COUNT(a) FROM JobApplication a WHERE a.jobPosting.employer.id = :employerId")
    long countByEmployerId(@Param("employerId") Long employerId);

    /**
     * Đếm đơn ứng tuyển theo trạng thái cho công ty
     * @author VY
     */
    @Query("SELECT COUNT(a) FROM JobApplication a WHERE a.jobPosting.employer.id = :employerId AND a.status = :status")
    long countByEmployerIdAndStatus(@Param("employerId") Long employerId, @Param("status") String status);
}
