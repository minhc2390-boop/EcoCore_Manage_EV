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

        log.info("Khởi tạo dữ liệu mẫu cho Website Tìm Việc Cho Sinh Viên Cần Thơ (UniWork Cần Thơ)...");

        // 1. Danh mục ngành nghề sinh viên Cần Thơ
        JobCategory catParttime = jobCategoryRepository.save(JobCategory.builder()
                .name("Phục Vụ & Pha Chế Cà Phê / Trà Sữa")
                .code("CAFE_BARISTA")
                .icon("bi-cup-hot")
                .description("Phù hợp làm thêm ca sáng/chiều/tối linh hoạt theo thời khóa biểu học tại Cần Thơ")
                .active(true)
                .build());

        JobCategory catIntern = jobCategoryRepository.save(JobCategory.builder()
                .name("Thực Tập Sinh CNTT & Kỹ Thuật (Internship)")
                .code("TECH_INTERNSHIP")
                .icon("bi-laptop")
                .description("Dành cho sinh viên CNTT các trường ĐH Cần Thơ, FPT Polytechnic, Kỹ Thuật Công Nghệ")
                .active(true)
                .build());

        JobCategory catRetail = jobCategoryRepository.save(JobCategory.builder()
                .name("Bán Hàng & Thu Ngân Siêu Thị / Shop")
                .code("RETAIL")
                .icon("bi-bag-check")
                .description("Làm việc tại các trung tâm thương mại Vincom, Lotte Mart, GO! Cần Thơ")
                .active(true)
                .build());

        JobCategory catTutor = jobCategoryRepository.save(JobCategory.builder()
                .name("Gia Sư & Trợ Giảng Trung Tâm")
                .code("TUTOR")
                .icon("bi-book")
                .description("Dành cho sinh viên có thế mạnh tiếng Anh, Toán, Lập trình căn bản")
                .active(true)
                .build());

        // 2. Tài khoản Super Admin điều hành hệ thống
        userRepository.save(User.builder()
                .username("superadmin")
                .password("123456")
                .fullName("Quản Trị Viên Tối Cao UniWork")
                .email("superadmin@uniwork.edu.vn")
                .phone("0912345678")
                .role(Role.SUPER_ADMIN)
                .active(true)
                .build());

        // 3. Tài khoản Sinh viên mẫu
        userRepository.save(User.builder()
                .username("sinhvien_cantho")
                .password("123456")
                .fullName("Nguyễn Văn Sinh Viên (FPT Poly Cần Thơ)")
                .email("sinhvienct@fpt.edu.vn")
                .phone("0939123456")
                .role(Role.STUDENT)
                .active(true)
                .build());

        // 4. Các Company Admin đại diện cho các doanh nghiệp tại Cần Thơ
        User empUser1 = userRepository.save(User.builder()
                .username("highlandscantho")
                .password("123456")
                .fullName("Admin Highlands Coffee Bến Ninh Kiều")
                .email("ninhkieu@highlandscoffee.com.vn")
                .phone("02923888999")
                .role(Role.COMPANY_ADMIN)
                .active(true)
                .build());

        Employer emp1 = employerRepository.save(Employer.builder()
                .companyName("Highlands Coffee Bến Ninh Kiều")
                .address("Đường Hai Bà Trưng, Phường Tân An, Quận Ninh Kiều, TP. Cần Thơ")
                .website("https://highlandscoffee.com.vn")
                .description("Chi nhánh Highlands Coffee bờ kè Bến Ninh Kiều tuyển dụng nhân sự part-time theo ca cho sinh viên.")
                .contactEmail("hr.cantho@highlandscoffee.com.vn")
                .user(empUser1)
                .build());

        User empUser2 = userRepository.save(User.builder()
                .username("fptsoftware_cantho")
                .password("123456")
                .fullName("Admin Tuyển Dụng FPT Software Cần Thơ")
                .email("tuyendung.cantho@fpt.com")
                .phone("02923777888")
                .role(Role.COMPANY_ADMIN)
                .active(true)
                .build());

        Employer emp2 = employerRepository.save(Employer.builder()
                .companyName("FPT Software Cần Thơ")
                .address("Đường 30/4, Phường Xuân Khánh, Quận Ninh Kiều, TP. Cần Thơ")
                .website("https://fptsoftware.com")
                .description("Văn phòng công nghệ phần mềm FPT tại Cần Thơ với các chương trình Fresher/Internship chuyên nghiệp.")
                .contactEmail("fresher.cantho@fpt.com")
                .user(empUser2)
                .build());

        User empUser3 = userRepository.save(User.builder()
                .username("gocantho")
                .password("123456")
                .fullName("Admin Siêu Thị GO! Cần Thơ")
                .email("hr@go-cantho.vn")
                .phone("02923666777")
                .role(Role.COMPANY_ADMIN)
                .active(true)
                .build());

        Employer emp3 = employerRepository.save(Employer.builder()
                .companyName("Đại Siêu Thị GO! Cần Thơ (Big C cũ)")
                .address("Lô số 1, KDC Hưng Phú 1, Phường Hưng Phú, Quận Cái Răng, TP. Cần Thơ")
                .website("https://go-vietnam.vn")
                .description("Trung tâm mua sắm và ẩm thực lớn hàng đầu khu vực Tây Nam Bộ tại Quận Cái Răng.")
                .contactEmail("tuyendung@go-cantho.vn")
                .user(empUser3)
                .build());

        // 5. Tin tuyển dụng mẫu thực tế tại TP. Cần Thơ
        jobPostingRepository.save(JobPosting.builder()
                .title("Nhân Viên Pha Chế / Phục Vụ Ca Xoay Sinh Viên (Bến Ninh Kiều)")
                .category(catParttime)
                .employer(emp1)
                .location("Quận Ninh Kiều, TP. Cần Thơ")
                .jobType("Part-time (Ca 4–5 tiếng)")
                .shiftSchedule("Ca sáng (07:00 - 11:30), Ca tối (17:30 - 22:30), đăng ký theo tuần")
                .salaryRange("23.000đ - 28.000đ / giờ + Thưởng doanh số")
                .description("Pha chế đồ uống, phục vụ khách du lịch và người dân tại Bến Ninh Kiều. Môi trường trẻ trung, năng động.")
                .requirements("Sinh viên ĐH Cần Thơ, FPT Poly Cần Thơ, Nam Cần Thơ... nhanh nhẹn, trung thực, không yêu cầu kinh nghiệm.")
                .benefits("Được đào tạo pha chế chuẩn chuỗi, giảm giá 50% đồ uống, hỗ trợ ký giấy xác nhận thực tập.")
                .deadline(LocalDate.now().plusDays(30))
                .status("ACTIVE")
                .viewCount(156)
                .createdAt(LocalDateTime.now())
                .build());

        jobPostingRepository.save(JobPosting.builder()
                .title("Thực Tập Sinh Java / Spring Boot Backend (Trợ Cấp 5–8 Triệu)")
                .category(catIntern)
                .employer(emp2)
                .location("Quận Ninh Kiều, TP. Cần Thơ")
                .jobType("Thực tập sinh (Internship)")
                .shiftSchedule("Linh hoạt 3–4 ngày/tuần theo lịch học kỳ")
                .salaryRange("5.000.000đ - 8.000.000đ / tháng")
                .description("Tham gia các dự án chuyển đổi số thực tế, làm việc với Spring Boot, JPA, SQL Server và REST API.")
                .requirements("Sinh viên chuyên ngành CNTT/Lập trình phần mềm, nắm vững Java căn bản và OOP.")
                .benefits("Có mentor hướng dẫn 1-1, trợ cấp thực tập hấp dẫn, cơ hội lên nhân viên chính thức.")
                .deadline(LocalDate.now().plusDays(45))
                .status("ACTIVE")
                .viewCount(310)
                .createdAt(LocalDateTime.now())
                .build());

        jobPostingRepository.save(JobPosting.builder()
                .title("Thu Ngân & Kiểm Kê Hàng Hóa Ca Tối / Cuối Tuần (GO! Cái Răng)")
                .category(catRetail)
                .employer(emp3)
                .location("Quận Cái Răng, TP. Cần Thơ")
                .jobType("Part-time (Ca linh hoạt)")
                .shiftSchedule("Ca tối (18:00 - 22:00) hoặc Thứ Bảy, Chủ Nhật")
                .salaryRange("25.000đ - 30.000đ / giờ")
                .description("Thực hiện thanh toán tại quầy thu ngân, hỗ trợ khách hàng sắp xếp quầy kệ hàng hóa gọn gàng.")
                .requirements("Trung thực, cẩn thận, ưu tiên sinh viên các trường gần khu vực Cái Răng và Ninh Kiều.")
                .benefits("Phụ cấp ăn ca, thưởng lễ tết theo quy định, tăng lương nếu gắn bó lâu dài.")
                .deadline(LocalDate.now().plusDays(25))
                .status("ACTIVE")
                .viewCount(185)
                .createdAt(LocalDateTime.now())
                .build());

        log.info("Khởi tạo dữ liệu mẫu UniWork Cần Thơ hoàn tất thành công!");
    }
}
