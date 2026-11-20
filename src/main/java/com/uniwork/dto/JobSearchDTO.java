package com.uniwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSearchDTO {
    private String keyword;
    private Long categoryId;
    private String level;         // Cấp bậc
    private String experience;    // Kinh nghiệm
    private String salary;        // Mức lương
    private String education;     // Học vấn
    private String jobType;       // Loại công việc
    private String postedWithin;  // Đăng trong
    private String location;

    public boolean hasActiveFilters() {
        return (keyword != null && !keyword.trim().isEmpty())
                || categoryId != null
                || (level != null && !level.trim().isEmpty())
                || (experience != null && !experience.trim().isEmpty())
                || (salary != null && !salary.trim().isEmpty())
                || (education != null && !education.trim().isEmpty())
                || (jobType != null && !jobType.trim().isEmpty())
                || (postedWithin != null && !postedWithin.trim().isEmpty())
                || (location != null && !location.trim().isEmpty());
    }
}
