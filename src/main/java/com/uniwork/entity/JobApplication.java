package com.uniwork.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @Column(length = 255)
    private String cvUrl;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String coverLetter;

    @Column(length = 255)
    private String studentScheduleNotes; // Ghi chú lịch rảnh/lịch học của sinh viên

    @Builder.Default
    @Column(length = 30, nullable = false)
    private String status = "PENDING"; // PENDING, REVIEWING, INTERVIEW, ACCEPTED, REJECTED

    @Builder.Default
    @Column(name = "applied_at")
    private LocalDateTime appliedAt = LocalDateTime.now();
}
