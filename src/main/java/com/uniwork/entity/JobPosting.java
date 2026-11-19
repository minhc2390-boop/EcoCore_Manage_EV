package com.uniwork.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity Tin Tuyển Dụng (JobPosting)
 * Khóa ngoại liên kết Doanh nghiệp (@ManyToOne Employer) và Danh mục ngành nghề (@ManyToOne JobCategory)
 * @author Hùng Anh - Company Admin Portal (Phần Tin Tuyển Dụng)
 */
@Entity
@Table(name = "job_postings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String description;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String requirements;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String benefits;

    @Column(length = 100)
    private String salaryRange;

    @Column(nullable = false, length = 150)
    private String location;

    /**
     * Hình thức làm việc: Part-time, Internship, Freelance, Ca xoay theo lịch học
     */
    @Column(nullable = false, length = 50)
    private String jobType;

    @Column(length = 100)
    private String shiftSchedule;

    private LocalDate deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private JobCategory category;

    @Builder.Default
    @Column(length = 20, nullable = false)
    private String status = "ACTIVE"; // ACTIVE, HIDDEN, CLOSED, EXPIRED

    @Builder.Default
    private Integer viewCount = 0;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Helper lấy thông tin User (Tài khoản Nhà tuyển dụng) đại diện công ty
     */
    public User getUser() {
        return employer != null ? employer.getUser() : null;
    }
}
