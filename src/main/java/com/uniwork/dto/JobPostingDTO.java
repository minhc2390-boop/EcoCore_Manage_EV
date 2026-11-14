package com.uniwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private String jobType;
    private String shiftSchedule;
    private LocalDate deadline;
    private Long categoryId;
    private Long employerId;
}
