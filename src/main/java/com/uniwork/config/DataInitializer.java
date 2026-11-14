package com.uniwork.config;

import com.uniwork.entity.*;
import com.uniwork.repository.EmployerRepository;
import com.uniwork.repository.JobCategoryRepository;
import com.uniwork.repository.JobPostingRepository;
import com.uniwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final JobPostingRepository jobPostingRepository;

    @Override
    public void run(String... args) {
        if (jobCategoryRepository.count() > 0) {
            return;
        }

        log.info("Khởi tạo dữ liệu mẫu cho Website Tìm Việc Cho Sinh Viên (StudentJobPortal)...");

        // 1. Danh mục ngành nghề phù hợp với sinh viên
        JobCategory catParttime = jobCategoryRepository.save(JobCategory.builder()
                .name("Việc Làm Bán Thời Gian (Part-time)")
                .code("PART_TIME")
                .icon("bi-clock-history")
                .description("Phù hợp làm thêm buổi tối hoặc cuối tuần theo ca linh hoạt")
                .active(true)
                .build());

        JobCategory catIntern = jobCategoryRepository.save(JobCategory.builder()
                .name("Thực Tập Sinh Doanh Nghiệp (Internship)")
                .code("INTERNSHIP")
                .icon("bi-mortarboard")
                .description("Cơ hội tích lũy kinh nghiệm thực tế có hỗ trợ phụ cấp lương")
                .active(true)
                .build());

        JobCategory catRemote = jobCategoryRepository.save(JobCategory.builder()
                .name("Việc Làm Từ Xa & Freelance")
                .code("REMOTE")
                .icon("bi-laptop")
                .description("Làm việc online tại nhà linh hoạt theo khối lượng công việc")
                .active(true)
                .build());

        JobCategory catTutor = jobCategoryRepository.save(JobCategory.builder()
                .name("Gia Sư & Trợ Giảng")
                .code("TUTOR")
                .icon("bi-book")
                .description("Dành cho sinh viên có thế mạnh về ngoại ngữ và kiến thức phổ thông")
                .active(true)
                .build());

        // 2. Tài khoản & Doanh nghiệp mẫu
        User empUser1 = userRepository.save(User.builder()
                .username("highlandscoffee")
                .password("123456")
                .fullName("Highlands Coffee Tuyển Dụng")
                .email("tuyendung@highlandscoffee.com.vn")
                .phone("0901234567")
                .role(Role.RECRUITER)
                .active(true)
                .build());

        Employer emp1 = employerRepository.save(Employer.builder()
                .companyName("Chuỗi Cà Phê Highlands Coffee")
                .address("Quận 1, TP. Hồ Chí Minh")
                .website("https://highlandscoffee.com.vn")
                .description("Chuỗi cà phê hàng đầu Việt Nam mang đến môi trường làm việc năng động cho sinh viên.")
                .contactEmail("hr@highlandscoffee.com.vn")
                .user(empUser1)
                .build());

        User empUser2 = userRepository.save(User.builder()
                .username("fptsoftware")
                .password("123456")
                .fullName("FPT Software Talent Acquisition")
                .email("recruitment@fpt.com")
                .phone("0988776655")
                .role(Role.RECRUITER)
                .active(true)
                .build());

        Employer emp2 = employerRepository.save(Employer.builder()
                .companyName("Tập Đoàn FPT Software")
                .address("Khu Công Nghệ Cao, TP. Thủ Đức")
                .website("https://fptsoftware.com")
                .description("Công ty phần mềm công nghệ số 1 Việt Nam với các chương trình Fresher/Internship chuyên nghiệp.")
                .contactEmail("fresher@fpt.com")
                .user(empUser2)
                .build());

        // 3. Tin tuyển dụng mẫu
        jobPostingRepository.save(JobPosting.builder()
                .title("Nhân Viên Barista / Phục Vụ Ca Linh Hoạt Cho Sinh Viên")
                .category(catParttime)
                .employer(emp1)
                .location("TP. Hồ Chí Minh (Các chi nhánh Q.1, Q.3, Q.10)")
                .jobType("Part-time (Ca 4 tiếng)")
                .shiftSchedule("Ca sáng (07:00 - 11:30), Ca tối (17:30 - 22:00) tự chọn")
                .salaryRange("25.000đ - 32.000đ / giờ + Thưởng")
                .description("Pha chế thức uống, order món tại quầy, phục vụ khách hàng chu đáo. Được đăng ký ca linh hoạt theo lịch học hàng tuần.")
                .requirements("Là sinh viên các trường ĐH/CĐ, nhanh nhẹn, thân thiện, trung thực, không yêu cầu kinh nghiệm.")
                .benefits("Được đào tạo pha chế bài bản, giảm 50% đồ uống toàn chuỗi, xét tăng lương định kỳ 3 tháng.")
                .deadline(LocalDate.now().plusDays(30))
                .status("ACTIVE")
                .viewCount(120)
                .createdAt(LocalDateTime.now())
                .build());

        jobPostingRepository.save(JobPosting.builder()
                .title("Thực Tập Sinh Java / Spring Boot (Có Trợ Cấp 6–10 Triệu/Tháng)")
                .category(catIntern)
                .employer(emp2)
                .location("TP. Thủ Đức, TP. Hồ Chí Minh")
                .jobType("Thực tập sinh (Internship)")
                .shiftSchedule("Tối thiểu 3–4 ngày/tuần (Linh động ngày thi)")
                .salaryRange("6.000.000đ - 10.000.000đ / tháng")
                .description("Tham gia phát triển dự án thực tế cùng các chuyên gia hàng đầu, rèn luyện kỹ năng Spring Boot, JPA, Microservices.")
                .requirements("Sinh viên năm 3, 4 hoặc mới tốt nghiệp ngành CNTT. Có kiến thức cơ bản về Java Core, Spring Framework và CSDL.")
                .benefits("Trợ cấp hàng tháng hấp dẫn, cơ hội trở thành nhân viên chính thức ngay sau khi kết thúc kỳ thực tập.")
                .deadline(LocalDate.now().plusDays(45))
                .status("ACTIVE")
                .viewCount(245)
                .createdAt(LocalDateTime.now())
                .build());

        jobPostingRepository.save(JobPosting.builder()
                .title("Cộng Tác Viên Viết Bài & Sáng Tạo Nội Dung Fanpage (Remote Online)")
                .category(catRemote)
                .employer(emp1)
                .location("Làm việc online tại nhà")
                .jobType("Làm việc từ xa (Remote)")
                .shiftSchedule("Tự do sắp xếp thời gian miễn đảm bảo KPI bài viết")
                .salaryRange("3.000.000đ - 5.000.000đ / tháng")
                .description("Lên ý tưởng kịch bản video ngắn TikTok và viết bài review đồ uống cho giới trẻ trên mạng xã hội.")
                .requirements("Sinh viên yêu thích sáng tạo nội dung, bắt trend nhanh, có khả năng viết lách cơ bản.")
                .benefits("Chủ động 100% thời gian, nhận nhuận bút hàng tuần, cấp chứng nhận kinh nghiệm làm việc.")
                .deadline(LocalDate.now().plusDays(20))
                .status("ACTIVE")
                .viewCount(89)
                .createdAt(LocalDateTime.now())
                .build());

        log.info("Khởi tạo dữ liệu mẫu hoàn tất thành công!");
    }
}
