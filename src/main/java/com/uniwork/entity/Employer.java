package com.uniwork.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity Nhà Tuyển Dụng / Doanh Nghiệp (Employer)
 * Đại diện công ty tuyển dụng tại TP. Cần Thơ, liên kết với tài khoản User (@OneToOne User)
 * @author Hùng Anh - Company Admin Portal (Phần Tin Tuyển Dụng)
 */
@Entity
@Table(name = "employers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String companyName;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String companyLogo;

    @Column(length = 150)
    private String website;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(length = 100)
    private String contactEmail;

    @Column(length = 20)
    private String contactPhone;

    @Builder.Default
    @Column(length = 20)
    private String status = "APPROVED"; // PENDING, APPROVED, LOCKED

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
