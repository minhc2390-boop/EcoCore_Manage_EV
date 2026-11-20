package com.uniwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object phục vụ form đăng tin & sửa tin tuyển dụng của Company Admin
 * @author Hùng Anh - Company Admin Portal (Phần Tin Tuyển Dụng)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostingDTO {
    private Long id;
    private String title;
    private String description;
    private String requirements;
    private String benefits;
    private String salaryRange;
    private String location;
    
    // Thuộc tính phục vụ chọn quận/huyện ở Cần Thơ (Hùng Anh)
    private String district;
    private String detailAddress;

    // Hình thức và ca làm việc (Hùng Anh)
    private String jobType;
    private String shiftSchedule;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadline;

    private Long categoryId;
    private Long employerId;
    private String status;
}
