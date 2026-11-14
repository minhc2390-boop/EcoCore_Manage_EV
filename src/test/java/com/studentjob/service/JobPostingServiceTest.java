package com.studentjob.service;

import com.studentjob.entity.JobPosting;
import com.studentjob.exception.ResourceNotFoundException;
import com.studentjob.repository.JobPostingRepository;
import com.studentjob.service.impl.JobPostingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test cho JobPostingService với JUnit 5 & Mockito")
class JobPostingServiceTest {

    @Mock
    private JobPostingRepository jobPostingRepository;

    @InjectMocks
    private JobPostingServiceImpl jobPostingService;

    private JobPosting sampleJob;

    @BeforeEach
    void setUp() {
        sampleJob = JobPosting.builder()
                .id(1L)
                .title("Nhân Viên Phục Vụ Ca Tối Cho Sinh Viên")
                .location("TP. Hồ Chí Minh")
                .jobType("Part-time")
                .status("ACTIVE")
                .salaryRange("25.000đ - 30.000đ / giờ")
                .build();
    }

    @Test
    @DisplayName("Kiểm tra lấy danh sách việc làm đang mở (ACTIVE)")
    void should_ReturnActiveJobs_When_GetAllActiveJobs() {
        when(jobPostingRepository.findByStatusOrderByCreatedAtDesc("ACTIVE"))
                .thenReturn(Arrays.asList(sampleJob));

        List<JobPosting> jobs = jobPostingService.getAllActiveJobs();

        assertNotNull(jobs);
        assertEquals(1, jobs.size());
        assertEquals("Nhân Viên Phục Vụ Ca Tối Cho Sinh Viên", jobs.get(0).getTitle());
        verify(jobPostingRepository, times(1)).findByStatusOrderByCreatedAtDesc("ACTIVE");
    }

    @Test
    @DisplayName("Kiểm tra tìm việc làm theo ID thành công")
    void should_ReturnJob_When_FindByIdExists() {
        when(jobPostingRepository.findById(1L)).thenReturn(Optional.of(sampleJob));

        JobPosting job = jobPostingService.getJobById(1L);

        assertNotNull(job);
        assertEquals(1L, job.getId());
        assertEquals("Part-time", job.getJobType());
    }

    @Test
    @DisplayName("Kiểm tra ném ResourceNotFoundException khi ID không tồn tại")
    void should_ThrowException_When_FindByIdNotExists() {
        when(jobPostingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jobPostingService.getJobById(999L));
    }
}
