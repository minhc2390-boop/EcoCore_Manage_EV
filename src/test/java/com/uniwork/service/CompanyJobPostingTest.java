package com.uniwork.service;

import com.uniwork.entity.Employer;
import com.uniwork.entity.JobCategory;
import com.uniwork.entity.JobPosting;
import com.uniwork.entity.Role;
import com.uniwork.entity.User;
import com.uniwork.exception.ResourceNotFoundException;
import com.uniwork.repository.JobPostingRepository;
import com.uniwork.service.impl.JobPostingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test cho Chức Năng Quản Lý Tin Tuyển Dụng Của Company Admin
 * @author Hùng Anh - Company Admin Portal (Phần Tin Tuyển Dụng)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test Company Admin Portal - Phụ trách: Hùng Anh")
class CompanyJobPostingTest {

    @Mock
    private JobPostingRepository jobPostingRepository;

    @InjectMocks
    private JobPostingServiceImpl jobPostingService;

    private User sampleCompanyAdmin;
    private Employer sampleEmployer;
    private JobCategory sampleCategory;
    private JobPosting sampleJob;

    @BeforeEach
    void setUp() {
        sampleCompanyAdmin = User.builder()
                .id(2L)
                .username("highlandscantho")
                .password("123456")
                .fullName("Admin Highlands Coffee Bến Ninh Kiều")
                .email("ninhkieu@highlandscoffee.com.vn")
                .phone("02923888999")
                .role(Role.COMPANY_ADMIN)
                .active(true)
                .build();

        sampleEmployer = Employer.builder()
                .id(10L)
                .companyName("Highlands Coffee Bến Ninh Kiều")
                .address("Đường Hai Bà Trưng, Phường Tân An, Quận Ninh Kiều, TP. Cần Thơ")
                .contactEmail("hr.cantho@highlandscoffee.com.vn")
                .user(sampleCompanyAdmin)
                .build();

        sampleCategory = JobCategory.builder()
                .id(100L)
                .name("Phục Vụ & Pha Chế Cà Phê / Trà Sữa")
                .code("CAFE_BARISTA")
                .active(true)
                .build();

        sampleJob = JobPosting.builder()
                .id(501L)
                .title("Nhân Viên Pha Chế / Phục Vụ Ca Xoay Sinh Viên (Bến Ninh Kiều)")
                .description("Pha chế thức uống theo công thức, phục vụ khách tham quan Bến Ninh Kiều...")
                .requirements("Sinh viên các trường ĐH Cần Thơ, FPT Polytechnic...")
                .benefits("Được đào tạo pha chế, giảm 50% đồ uống...")
                .salaryRange("23.000đ - 28.000đ / giờ")
                .location("Quận Ninh Kiều, TP. Cần Thơ")
                .jobType("Part-time (Bán thời gian)")
                .shiftSchedule("Ca sáng (07:00 - 11:30), Ca tối (17:30 - 22:30)")
                .deadline(LocalDate.now().plusDays(30))
                .status("ACTIVE")
                .viewCount(120)
                .createdAt(LocalDateTime.now())
                .employer(sampleEmployer)
                .category(sampleCategory)
                .build();
    }

    @Test
    @DisplayName("Kiểm tra đăng bài tuyển dụng theo ca và quận/huyện Cần Thơ thành công")
    void should_CreateJobSuccessfully_WithShiftAndCanThoDistrict() {
        when(jobPostingRepository.save(any(JobPosting.class))).thenReturn(sampleJob);

        JobPosting saved = jobPostingService.saveJob(sampleJob);

        assertNotNull(saved);
        assertEquals("Nhân Viên Pha Chế / Phục Vụ Ca Xoay Sinh Viên (Bến Ninh Kiều)", saved.getTitle());
        assertTrue(saved.getLocation().contains("Quận Ninh Kiều"));
        assertTrue(saved.getShiftSchedule().contains("Ca sáng"));
        assertEquals("ACTIVE", saved.getStatus());
        assertEquals(sampleEmployer.getId(), saved.getEmployer().getId());
        verify(jobPostingRepository, times(1)).save(sampleJob);
    }

    @Test
    @DisplayName("Kiểm tra lấy danh sách bài đăng của riêng công ty mình")
    void should_GetJobsByEmployerId_OnlyReturnEmployerJobs() {
        when(jobPostingRepository.findByEmployerIdOrderByCreatedAtDesc(10L))
                .thenReturn(List.of(sampleJob));

        List<JobPosting> jobs = jobPostingService.getJobsByEmployerId(10L);

        assertNotNull(jobs);
        assertEquals(1, jobs.size());
        assertEquals(10L, jobs.get(0).getEmployer().getId());
        verify(jobPostingRepository, times(1)).findByEmployerIdOrderByCreatedAtDesc(10L);
    }

    @Test
    @DisplayName("Kiểm tra chức năng ẩn tin tuyển dụng (ACTIVE -> HIDDEN)")
    void should_ToggleJobStatus_FromActiveToHidden() {
        when(jobPostingRepository.findByIdAndEmployerId(501L, 10L))
                .thenReturn(Optional.of(sampleJob));
        when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobPosting result = jobPostingService.toggleJobStatus(501L, 10L);

        assertEquals("HIDDEN", result.getStatus());
        verify(jobPostingRepository, times(1)).save(sampleJob);
    }

    @Test
    @DisplayName("Kiểm tra chức năng kích hoạt lại tin tuyển dụng đã ẩn (HIDDEN -> ACTIVE)")
    void should_ToggleJobStatus_FromHiddenToActive() {
        sampleJob.setStatus("HIDDEN");
        when(jobPostingRepository.findByIdAndEmployerId(501L, 10L))
                .thenReturn(Optional.of(sampleJob));
        when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobPosting result = jobPostingService.toggleJobStatus(501L, 10L);

        assertEquals("ACTIVE", result.getStatus());
        verify(jobPostingRepository, times(1)).save(sampleJob);
    }

    @Test
    @DisplayName("Kiểm tra chức năng gia hạn tin tuyển dụng thêm 30 ngày")
    void should_ExtendJobDeadline_Successfully() {
        LocalDate oldDeadline = LocalDate.now().plusDays(10);
        sampleJob.setDeadline(oldDeadline);

        when(jobPostingRepository.findByIdAndEmployerId(501L, 10L))
                .thenReturn(Optional.of(sampleJob));
        when(jobPostingRepository.save(any(JobPosting.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobPosting result = jobPostingService.extendJobDeadline(501L, 10L, 30);

        assertEquals(oldDeadline.plusDays(30), result.getDeadline());
        verify(jobPostingRepository, times(1)).save(sampleJob);
    }

    @Test
    @DisplayName("Bảo mật: Ném ngoại lệ khi truy cập hoặc sửa bài đăng của công ty khác")
    void should_ThrowException_WhenAccessingJobOfAnotherCompany() {
        Long strangerEmployerId = 999L;
        when(jobPostingRepository.findByIdAndEmployerId(501L, strangerEmployerId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            jobPostingService.getJobByIdAndEmployerId(501L, strangerEmployerId);
        });

        verify(jobPostingRepository, never()).save(any());
    }
}
