package com.uniwork.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity Đánh giá & Chấm sao Doanh nghiệp (Company Review)
 * Dành cho sinh viên đánh giá độ uy tín, môi trường làm việc và cảnh báo trả lương trễ/bóc lột.
 */
@Entity
@Table(name = "company_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @Column(nullable = false, length = 100)
    private String studentName;

    @Column(nullable = false)
    private Integer rating; // 1 đến 5 sao

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(length = 50)
    private String salaryRating; // "Đúng hạn", "Trả trễ", "Rõ ràng minh bạch"

    @Column(length = 50)
    private String environmentRating; // "Thân thiện hỗ trợ", "Bóc lột / quá tải", "Chuyên nghiệp"

    @Builder.Default
    @Column(nullable = false)
    private Boolean verifiedStudent = true;

    @Builder.Default
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
