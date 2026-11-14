package com.studentjob.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplicationDTO {
    private Long jobPostingId;
    private Long studentId;
    private String coverLetter;
    private String studentScheduleNotes;
    private String cvUrl;
}
