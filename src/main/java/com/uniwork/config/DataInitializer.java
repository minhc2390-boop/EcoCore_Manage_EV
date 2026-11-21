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
import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final JobPostingRepository jobPostingRepository;
    private final com.uniwork.repository.CompanyReviewRepository companyReviewRepository;

    @Override
    public void run(String... args) {
        log.info("Khởi tạo dữ liệu mẫu cho Website Tìm Việc Cho Sinh Viên Cần Thơ (UniWork Cần Thơ)...");

        // 1. Danh mục ngành nghề sinh viên Cần Thơ
        JobCategory catParttime;
        JobCategory catIntern;
        JobCategory catRetail;
        JobCategory catTutor;

        if (jobCategoryRepository.count() == 0) {
            catParttime = jobCategoryRepository.save(JobCategory.builder()
                    .name("Phục Vụ & Pha Chế Cà Phê / Trà Sữa")
                    .code("CAFE_BARISTA")
                    .icon("bi-cup-hot")
                    .description("Phù hợp làm thêm ca sáng/chiều/tối linh hoạt theo thời khóa biểu học tại Cần Thơ")
                    .active(true)
                    .build());

            catIntern = jobCategoryRepository.save(JobCategory.builder()
                    .name("Thực Tập Sinh CNTT & Kỹ Thuật (Internship)")
                    .code("TECH_INTERNSHIP")
                    .icon("bi-laptop")
                    .description("Dành cho sinh viên CNTT các trường ĐH Cần Thơ, FPT Polytechnic, Kỹ Thuật Công Nghệ")
                    .active(true)
                    .build());

            catRetail = jobCategoryRepository.save(JobCategory.builder()
                    .name("Bán Hàng & Thu Ngân Siêu Thị / Shop")
                    .code("RETAIL")
                    .icon("bi-bag-check")
                    .description("Làm việc tại các trung tâm thương mại Vincom, Lotte Mart, GO! Cần Thơ")
                    .active(true)
                    .build());

            catTutor = jobCategoryRepository.save(JobCategory.builder()
                    .name("Gia Sư & Trợ Giảng Trung Tâm")
                    .code("TUTOR")
                    .icon("bi-book")
                    .description("Dành cho sinh viên có thế mạnh tiếng Anh, Toán, Lập trình căn bản")
                    .active(true)
                    .build());
        } else {
            List<JobCategory> allCats = jobCategoryRepository.findAll();
            catParttime = allCats.get(0);
            catIntern = allCats.size() > 1 ? allCats.get(1) : catParttime;
            catRetail = allCats.size() > 2 ? allCats.get(2) : catParttime;
            catTutor = allCats.size() > 3 ? allCats.get(3) : catParttime;
        }

        // 2. Tài khoản Super Admin điều hành hệ thống
        if (userRepository.findByUsername("superadmin").isEmpty()) {
            userRepository.save(User.builder()
                    .username("superadmin")
                    .password("123456")
                    .fullName("Quản Trị Viên Tối Cao UniWork")
                    .email("superadmin@uniwork.edu.vn")
                    .phone("0912345678")
                    .role(Role.SUPER_ADMIN)
                    .active(true)
                    .build());
        }

        // 3. Tài khoản Sinh viên mẫu
        if (userRepository.findByUsername("sinhvien_cantho").isEmpty()) {
            userRepository.save(User.builder()
                    .username("sinhvien_cantho")
                    .password("123456")
                    .fullName("Nguyễn Văn Sinh Viên (FPT Poly Cần Thơ)")
                    .email("sinhvienct@fpt.edu.vn")
                    .phone("0939123456")
                    .role(Role.STUDENT)
                    .active(true)
                    .build());
        }
        if (userRepository.findByUsername("tran_thanh_nga").isEmpty()) {
            userRepository.save(User.builder()
                    .username("tran_thanh_nga")
                    .password("123456")
                    .fullName("Trần Thanh Nga (ĐH Cần Thơ)")
                    .email("thanhnga.ctu@gmail.com")
                    .phone("0939888123")
                    .role(Role.STUDENT)
                    .active(true)
                    .build());
        }
        if (userRepository.findByUsername("le_hoang_nam").isEmpty()) {
            userRepository.save(User.builder()
                    .username("le_hoang_nam")
                    .password("123456")
                    .fullName("Lê Hoàng Nam (ĐH Kỹ Thuật Công Nghệ Cần Thơ)")
                    .email("namlh.ctuet@gmail.com")
                    .phone("0939555432")
                    .role(Role.STUDENT)
                    .active(true)
                    .build());
        }
        if (userRepository.findByUsername("pham_minh_khoi").isEmpty()) {
            userRepository.save(User.builder()
                    .username("pham_minh_khoi")
                    .password("123456")
                    .fullName("Phạm Minh Khôi (Cao Đẳng Cần Thơ)")
                    .email("khoipm.cdct@gmail.com")
                    .phone("0909112233")
                    .role(Role.STUDENT)
                    .active(true)
                    .build());
        }
        if (userRepository.findByUsername("nguyen_thi_mai").isEmpty()) {
            userRepository.save(User.builder()
                    .username("nguyen_thi_mai")
                    .password("123456")
                    .fullName("Nguyễn Thị Mai (ĐH Nam Cần Thơ)")
                    .email("maintt.nctu@gmail.com")
                    .phone("0918776655")
                    .role(Role.STUDENT)
                    .active(false)
                    .build());
        }

        // 4. Các Company Admin đại diện cho các doanh nghiệp tại Cần Thơ
        Employer emp1;
        Employer emp2;
        Employer emp3;

        Optional<User> u1Opt = userRepository.findByUsername("highlandscantho");
        if (u1Opt.isEmpty()) {
            User empUser1 = userRepository.save(User.builder()
                    .username("highlandscantho")
                    .password("123456")
                    .fullName("Admin Highlands Coffee Bến Ninh Kiều")
                    .email("ninhkieu@highlandscoffee.com.vn")
                    .phone("02923888999")
                    .role(Role.COMPANY_ADMIN)
                    .active(true)
                    .build());

            emp1 = employerRepository.save(Employer.builder()
                    .companyName("Highlands Coffee Bến Ninh Kiều")
                    .address("Đường Hai Bà Trưng, Phường Tân An, Quận Ninh Kiều, TP. Cần Thơ")
                    .website("https://highlandscoffee.com.vn")
                    .description("Chi nhánh Highlands Coffee bờ kè Bến Ninh Kiều tuyển dụng nhân sự part-time theo ca cho sinh viên.")
                    .contactEmail("hr.cantho@highlandscoffee.com.vn")
                    .user(empUser1)
                    .build());
        } else {
            emp1 = employerRepository.findByUserId(u1Opt.get().getId()).orElse(null);
        }

        Optional<User> u2Opt = userRepository.findByUsername("fptsoftware_cantho");
        if (u2Opt.isEmpty()) {
            User empUser2 = userRepository.save(User.builder()
                    .username("fptsoftware_cantho")
                    .password("123456")
                    .fullName("Admin Tuyển Dụng FPT Software Cần Thơ")
                    .email("tuyendung.cantho@fpt.com")
                    .phone("02923777888")
                    .role(Role.COMPANY_ADMIN)
                    .active(true)
                    .build());

            emp2 = employerRepository.save(Employer.builder()
                    .companyName("FPT Software Cần Thơ")
                    .address("Đường 30/4, Phường Xuân Khánh, Quận Ninh Kiều, TP. Cần Thơ")
                    .website("https://fptsoftware.com")
                    .description("Văn phòng công nghệ phần mềm FPT tại Cần Thơ với các chương trình Fresher/Internship chuyên nghiệp.")
                    .contactEmail("fresher.cantho@fpt.com")
                    .user(empUser2)
                    .build());
        } else {
            emp2 = employerRepository.findByUserId(u2Opt.get().getId()).orElse(null);
        }

        Optional<User> u3Opt = userRepository.findByUsername("gocantho");
        if (u3Opt.isEmpty()) {
            User empUser3 = userRepository.save(User.builder()
                    .username("gocantho")
                    .password("123456")
                    .fullName("Admin Siêu Thị GO! Cần Thơ")
                    .email("hr@go-cantho.vn")
                    .phone("02923666777")
                    .role(Role.COMPANY_ADMIN)
                    .active(true)
                    .build());

            emp3 = employerRepository.save(Employer.builder()
                    .companyName("Đại Siêu Thị GO! Cần Thơ (Big C cũ)")
                    .address("Lô số 1, KDC Hưng Phú 1, Phường Hưng Phú, Quận Cái Răng, TP. Cần Thơ")
                    .website("https://go-vietnam.vn")
                    .description("Trung tâm mua sắm và ẩm thực lớn hàng đầu khu vực Tây Nam Bộ tại Quận Cái Răng.")
                    .contactEmail("tuyendung@go-cantho.vn")
                    .user(empUser3)
                    .build());
        } else {
            emp3 = employerRepository.findByUserId(u3Opt.get().getId()).orElse(null);
        }

        // Tạo thêm Nhà tuyển dụng 4: Phúc Long Vincom Xuân Khánh
        Employer emp4;
        Optional<User> u4Opt = userRepository.findByUsername("phuclong_cantho");
        if (u4Opt.isEmpty()) {
            User empUser4 = userRepository.save(User.builder()
                    .username("phuclong_cantho")
                    .password("123456")
                    .fullName("Admin Tuyển Dụng Phúc Long Cần Thơ")
                    .email("tuyendung@phuclong.com.vn")
                    .phone("02923555666")
                    .role(Role.COMPANY_ADMIN)
                    .active(true)
                    .build());

            emp4 = employerRepository.save(Employer.builder()
                    .companyName("Phúc Long Coffee & Tea Vincom Xuân Khánh")
                    .address("209 Đường 30 Tháng 4, Phường Xuân Khánh, Quận Ninh Kiều, TP. Cần Thơ")
                    .website("https://phuclong.com.vn")
                    .description("Cửa hàng trà và cà phê Phúc Long hàng đầu tại TTTM Vincom Plaza Xuân Khánh Cần Thơ.")
                    .contactEmail("hr.cantho@phuclong.com.vn")
                    .user(empUser4)
                    .build());
        } else {
            emp4 = employerRepository.findByUserId(u4Opt.get().getId()).orElse(emp1);
        }

        // Tạo thêm Nhà tuyển dụng 5: CGV Cinema Vincom Hùng Vương
        Employer emp5;
        Optional<User> u5Opt = userRepository.findByUsername("cgv_cantho");
        if (u5Opt.isEmpty()) {
            User empUser5 = userRepository.save(User.builder()
                    .username("cgv_cantho")
                    .password("123456")
                    .fullName("Admin Tuyển Dụng CGV Cần Thơ")
                    .email("cinema.cantho@cgv.vn")
                    .phone("02923777999")
                    .role(Role.COMPANY_ADMIN)
                    .active(true)
                    .build());

            emp5 = employerRepository.save(Employer.builder()
                    .companyName("Cụm Rạp Chiếu Phim CGV Vincom Hùng Vương")
                    .address("Số 2 Đường Hùng Vương, Phường Thới Bình, Quận Ninh Kiều, TP. Cần Thơ")
                    .website("https://cgv.vn")
                    .description("Cụm rạp chiếu phim chuẩn quốc tế tại Cần Thơ với môi trường làm việc sôi động cho sinh viên.")
                    .contactEmail("tuyendung.cantho@cgv.vn")
                    .user(empUser5)
                    .build());
        } else {
            emp5 = employerRepository.findByUserId(u5Opt.get().getId()).orElse(emp1);
        }

        // Tạo thêm Nhà tuyển dụng 6: Trung Tâm Anh Ngữ VUS Cần Thơ
        Employer emp6;
        Optional<User> u6Opt = userRepository.findByUsername("vus_cantho");
        if (u6Opt.isEmpty()) {
            User empUser6 = userRepository.save(User.builder()
                    .username("vus_cantho")
                    .password("123456")
                    .fullName("Admin Tuyển Dụng VUS Cần Thơ")
                    .email("hr.cantho@vus-etsc.edu.vn")
                    .phone("02923888777")
                    .role(Role.COMPANY_ADMIN)
                    .active(true)
                    .build());

            emp6 = employerRepository.save(Employer.builder()
                    .companyName("Hệ Thống Anh Văn Hội Việt Mỹ VUS Cần Thơ")
                    .address("Đường 3 Tháng 2, Phường Xuân Khánh, Quận Ninh Kiều, TP. Cần Thơ")
                    .website("https://vus.edu.vn")
                    .description("Tổ chức đào tạo tiếng Anh uy tín hàng đầu cho học sinh và sinh viên ĐBSCL.")
                    .contactEmail("recruitment.cantho@vus-etsc.edu.vn")
                    .user(empUser6)
                    .build());
        } else {
            emp6 = employerRepository.findByUserId(u6Opt.get().getId()).orElse(emp2);
        }

        // Tạo thêm Nhà tuyển dụng 7: Lotte Mart Cần Thơ
        Employer emp7;
        Optional<User> u7Opt = userRepository.findByUsername("lottemart_cantho");
        if (u7Opt.isEmpty()) {
            User empUser7 = userRepository.save(User.builder()
                    .username("lottemart_cantho")
                    .password("123456")
                    .fullName("Admin Tuyển Dụng Lotte Mart Cần Thơ")
                    .email("hr.cantho@lotte.vn")
                    .phone("02923999888")
                    .role(Role.COMPANY_ADMIN)
                    .active(true)
                    .build());

            emp7 = employerRepository.save(Employer.builder()
                    .companyName("Trung Tâm Thương Mại & Siêu Thị Lotte Mart Cần Thơ")
                    .address("Số 84 Đường Mậu Thân, Phường An Hòa, Quận Ninh Kiều, TP. Cần Thơ")
                    .website("https://lottemart.com.vn")
                    .description("Đại siêu thị Hàn Quốc tại khu vực Mậu Thân với hàng ngàn việc làm bán thời gian năng động.")
                    .contactEmail("recruitment.cantho@lotte.vn")
                    .user(empUser7)
                    .build());
        } else {
            emp7 = employerRepository.findByUserId(u7Opt.get().getId()).orElse(emp3);
        }

        // 5. Danh sách 24 tin tuyển dụng thực tế tại Cần Thơ (Phục vụ phân trang 10 tin/trang)
        if (jobPostingRepository.count() < 15) {
            // Đảm bảo có sẵn employer hợp lệ
            Employer fallbackEmp = (emp1 != null) ? emp1 : (emp2 != null ? emp2 : emp3);
            Employer e1 = (emp1 != null) ? emp1 : fallbackEmp;
            Employer e2 = (emp2 != null) ? emp2 : fallbackEmp;
            Employer e3 = (emp3 != null) ? emp3 : fallbackEmp;
            Employer e4 = (emp4 != null) ? emp4 : fallbackEmp;
            Employer e5 = (emp5 != null) ? emp5 : fallbackEmp;
            Employer e6 = (emp6 != null) ? emp6 : fallbackEmp;
            Employer e7 = (emp7 != null) ? emp7 : fallbackEmp;

            // Tin 1
            jobPostingRepository.save(JobPosting.builder()
                    .title("Nhân Viên Pha Chế / Phục Vụ Ca Xoay Sinh Viên (Bến Ninh Kiều)")
                    .category(catParttime)
                    .employer(e1)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("Ca sáng (07:00 - 11:30), Ca tối (17:30 - 22:30), đăng ký theo tuần")
                    .salaryRange("23.000đ - 28.000đ / giờ + Thưởng doanh số")
                    .description("Pha chế đồ uống, phục vụ khách du lịch và người dân tại Bến Ninh Kiều. Môi trường trẻ trung, năng động.")
                    .requirements("Sinh viên ĐH Cần Thơ, FPT Poly Cần Thơ, Nam Cần Thơ... nhanh nhẹn, trung thực, không yêu cầu kinh nghiệm.")
                    .benefits("Được đào tạo pha chế chuẩn chuỗi, giảm giá 50% đồ uống, hỗ trợ ký giấy xác nhận thực tập.")
                    .deadline(LocalDate.now().plusDays(30))
                    .status("ACTIVE")
                    .viewCount(156)
                    .createdAt(LocalDateTime.now().minusHours(1))
                    .build());

            // Tin 2
            jobPostingRepository.save(JobPosting.builder()
                    .title("Thực Tập Sinh Java / Spring Boot Backend (Trợ Cấp 5–8 Triệu)")
                    .category(catIntern)
                    .employer(e2)
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
                    .createdAt(LocalDateTime.now().minusHours(2))
                    .build());

            // Tin 3
            jobPostingRepository.save(JobPosting.builder()
                    .title("Thu Ngân & Kiểm Kê Hàng Hóa Ca Tối / Cuối Tuần (GO! Cái Răng)")
                    .category(catRetail)
                    .employer(e3)
                    .location("Quận Cái Răng, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("Ca tối (18:00 - 22:00) hoặc Thứ Bảy, Chủ Nhật")
                    .salaryRange("25.000đ - 30.000đ / giờ")
                    .description("Thực hiện thanh toán tại quầy thu ngân, hỗ trợ khách hàng sắp xếp quầy kệ hàng hóa gọn gàng.")
                    .requirements("Trung thực, cẩn thận, ưu tiên sinh viên các trường gần khu vực Cái Răng và Ninh Kiều.")
                    .benefits("Phụ cấp ăn ca, thưởng lễ tết theo quy định, tăng lương nếu gắn bó lâu dài.")
                    .deadline(LocalDate.now().plusDays(25))
                    .status("ACTIVE")
                    .viewCount(185)
                    .createdAt(LocalDateTime.now().minusHours(3))
                    .build());

            // Tin 4
            jobPostingRepository.save(JobPosting.builder()
                    .title("Nhân Viên Pha Chế Trà Sữa & Thu Ngân Ca Sáng/Tối (Phúc Long Vincom)")
                    .category(catParttime)
                    .employer(e4)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("Ca sáng (08:00 - 13:00) hoặc Ca tối (17:30 - 22:30)")
                    .salaryRange("24.000đ - 29.000đ / giờ")
                    .description("Nhận order, pha chế các dòng trà sữa và cà phê Phúc Long theo công thức chuẩn chuỗi.")
                    .requirements("Ngoại hình sáng, giao tiếp thân thiện, không cần kinh nghiệm sẽ được đào tạo bài bản.")
                    .benefits("Phụ cấp gửi xe, giảm 40% menu toàn hệ thống, đăng ký ca theo tuần linh hoạt.")
                    .deadline(LocalDate.now().plusDays(28))
                    .status("ACTIVE")
                    .viewCount(240)
                    .createdAt(LocalDateTime.now().minusHours(4))
                    .build());

            // Tin 5
            jobPostingRepository.save(JobPosting.builder()
                    .title("Nhân Viên Soát Vé & Bán Bắp Nước Rạp Chiếu Phim (CGV Hùng Vương)")
                    .category(catRetail)
                    .employer(e5)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("Ca chiều tối (16:30 - 21:30) & Cuối tuần")
                    .salaryRange("24.000đ - 27.000đ / giờ")
                    .description("Soát vé khách hàng vào rạp chiếu, hỗ trợ bán bắp rang bơ, nước giải khát tại quầy Concession.")
                    .requirements("Trung thực, vui vẻ, thích xem phim, ưu tiên sinh viên năm 1, 2, 3.")
                    .benefits("Xem phim miễn phí định kỳ tại CGV Cần Thơ, môi trường trẻ trung cực kỳ thân thiện.")
                    .deadline(LocalDate.now().plusDays(35))
                    .status("ACTIVE")
                    .viewCount(412)
                    .createdAt(LocalDateTime.now().minusHours(5))
                    .build());

            // Tin 6
            jobPostingRepository.save(JobPosting.builder()
                    .title("Trợ Giảng Tiếng Anh Lớp Thiếu Nhi Ca Tối 2-4-6 (VUS Cần Thơ)")
                    .category(catTutor)
                    .employer(e6)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("17:30 - 20:30 (Thứ 2, 4, 6 hoặc Thứ 3, 5, 7)")
                    .salaryRange("45.000đ - 65.000đ / giờ")
                    .description("Hỗ trợ giáo viên bản ngữ quản lý lớp học, kèm cặp học viên nhí trong các hoạt động tương tác.")
                    .requirements("Sinh viên tiếng Anh hoặc có chứng chỉ IELTS 6.0 / TOEIC 650 trở lên, yêu trẻ em.")
                    .benefits("Luyện giao tiếp tiếng Anh trực tiếp với giáo viên nước ngoài, cấp chứng nhận kinh nghiệm giảng dạy.")
                    .deadline(LocalDate.now().plusDays(20))
                    .status("ACTIVE")
                    .viewCount(198)
                    .createdAt(LocalDateTime.now().minusHours(6))
                    .build());

            // Tin 7
            jobPostingRepository.save(JobPosting.builder()
                    .title("Nhân Viên Tiếp Thị & Sắp Xếp Gian Hàng Bánh Kẹo (Lotte Mart Mậu Thân)")
                    .category(catRetail)
                    .employer(e7)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("Ca xoay: 07:30 - 12:00 hoặc 13:00 - 17:30")
                    .salaryRange("25.000đ - 28.000đ / giờ")
                    .description("Chăm sóc quầy kệ, kiểm tra hạn sử dụng sản phẩm và tư vấn hàng hóa cho khách mua sắm.")
                    .requirements("Sức khỏe tốt, chăm chỉ, có phương tiện đi lại tự túc.")
                    .benefits("Thưởng doanh số siêu thị hàng tháng, cấp đồng phục miễn phí, môi trường làm việc máy lạnh mát mẻ.")
                    .deadline(LocalDate.now().plusDays(18))
                    .status("ACTIVE")
                    .viewCount(145)
                    .createdAt(LocalDateTime.now().minusHours(7))
                    .build());

            // Tin 8
            jobPostingRepository.save(JobPosting.builder()
                    .title("Thực Tập Sinh Lập Trình Frontend ReactJS / TypeScript (Có Trợ Cấp)")
                    .category(catIntern)
                    .employer(e2)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Thực tập sinh (Internship)")
                    .shiftSchedule("Full-time hoặc Part-time linh hoạt 4 buổi/tuần")
                    .salaryRange("5.500.000đ - 7.500.000đ / tháng")
                    .description("Xây dựng giao diện Web App hiện đại với ReactJS, TailwindCSS, gọi API RESTful từ Spring Boot backend.")
                    .requirements("Sinh viên CNTT năm 3 hoặc năm cuối, biết cơ bản HTML/CSS/JS và thư viện React.")
                    .benefits("Được cấp laptop xịn, tham gia teambuilding, hỗ trợ dấu mộc báo cáo thực tập tốt nghiệp.")
                    .deadline(LocalDate.now().plusDays(40))
                    .status("ACTIVE")
                    .viewCount(355)
                    .createdAt(LocalDateTime.now().minusHours(8))
                    .build());

            // Tin 9
            jobPostingRepository.save(JobPosting.builder()
                    .title("Gia Sư Dạy Kèm Toán & Khoa Học Cấp 2 Tại Nhà (Quận Cái Răng)")
                    .category(catTutor)
                    .employer(e6)
                    .location("Quận Cái Răng, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("18:30 - 20:30 (3 buổi/tuần)")
                    .salaryRange("100.000đ - 140.000đ / buổi (2 tiếng)")
                    .description("Kèm cặp 1-1 cho học sinh lớp 7-8 các môn Toán và Khoa học tự nhiên, hỗ trợ giải bài tập về nhà.")
                    .requirements("Sinh viên Sư phạm Toán, Kỹ thuật hoặc có điểm thi đại học môn Toán từ 8.0 trở lên.")
                    .benefits("Thu nhập ổn định, gia đình học sinh hỗ trợ tiền xăng xe, thời gian học tập linh động.")
                    .deadline(LocalDate.now().plusDays(15))
                    .status("ACTIVE")
                    .viewCount(172)
                    .createdAt(LocalDateTime.now().minusHours(9))
                    .build());

            // Tin 10
            jobPostingRepository.save(JobPosting.builder()
                    .title("Nhân Viên Order & Thu Ngân Ca Trưa 11:30 - 15:30 (Highlands Cần Thơ)")
                    .category(catParttime)
                    .employer(e1)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("Ca trưa (11:30 - 15:30) từ Thứ 2 đến Thứ 6")
                    .salaryRange("24.000đ - 28.000đ / giờ")
                    .description("Vận hành hệ thống máy tính tiền POS, đón tiếp khách hàng văn phòng ăn trưa và uống cà phê.")
                    .requirements("Nhanh nhẹn, cẩn thận tiền bạc, ưu tiên sinh viên học ca sáng xong làm ca trưa.")
                    .benefits("Bao cơm trưa, giảm giá đồ uống chuỗi Highlands, nhận lương đúng ngày mùng 5 hàng tháng.")
                    .deadline(LocalDate.now().plusDays(22))
                    .status("ACTIVE")
                    .viewCount(168)
                    .createdAt(LocalDateTime.now().minusHours(10))
                    .build());

            // Tin 11 (Bắt đầu trang 2)
            jobPostingRepository.save(JobPosting.builder()
                    .title("Nhân Viên Đóng Gói Giỏ Quà Khuyến Mãi (GO! Hưng Phú Cần Thơ)")
                    .category(catRetail)
                    .employer(e3)
                    .location("Quận Cái Răng, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("Ca sáng (08:00 - 12:00) hoặc Ca chiều (13:30 - 17:30)")
                    .salaryRange("25.000đ - 29.000đ / giờ")
                    .description("Thực hiện đóng gói các giỏ quà tặng, kiểm tra tem nhãn mã vạch tại khu vực kho trung chuyển.")
                    .requirements("Khéo tay, cẩn thận, không yêu cầu bằng cấp hay kinh nghiệm.")
                    .benefits("Hỗ trợ ăn giữa ca, công việc nhẹ nhàng trong kho mát, có thể đăng ký làm theo ngày.")
                    .deadline(LocalDate.now().plusDays(26))
                    .status("ACTIVE")
                    .viewCount(132)
                    .createdAt(LocalDateTime.now().minusHours(11))
                    .build());

            // Tin 12
            jobPostingRepository.save(JobPosting.builder()
                    .title("Thực Tập Sinh Kiểm Thử Phần Mềm QA / QC (FPT Software Cần Thơ)")
                    .category(catIntern)
                    .employer(e2)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Thực tập sinh (Internship)")
                    .shiftSchedule("Giờ hành chính: 08:30 - 17:30 (Thứ 2 - Thứ 6)")
                    .salaryRange("5.000.000đ - 7.000.000đ / tháng")
                    .description("Viết test case, thực thi test thủ công (Manual Test) và học hỏi tự động hóa kiểm thử với Selenium.")
                    .requirements("Sinh viên CNTT/Hệ thống thông tin, tư duy logic tốt, cẩn thận và kiên nhẫn.")
                    .benefits("Đào tạo bài bản theo quy trình CMMI Level 5, hỗ trợ thi chứng chỉ quốc tế ISTQB.")
                    .deadline(LocalDate.now().plusDays(32))
                    .status("ACTIVE")
                    .viewCount(290)
                    .createdAt(LocalDateTime.now().minusHours(12))
                    .build());

            // Tin 13
            jobPostingRepository.save(JobPosting.builder()
                    .title("Nhân Viên Hướng Dẫn Phòng Chiếu & Vệ Sinh Rạp Ca Tối (CGV Cần Thơ)")
                    .category(catRetail)
                    .employer(e5)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("18:00 - 22:30 (Đăng ký 4-5 buổi/tuần)")
                    .salaryRange("23.000đ - 26.000đ / giờ")
                    .description("Kiểm tra phòng chiếu phim trước và sau suất chiếu, hỗ trợ hướng dẫn khách tìm đúng số ghế.")
                    .requirements("Sạch sẽ, nhanh nhẹn, trung thực, có thái độ phục vụ chu đáo.")
                    .benefits("Thưởng vé xem phim hàng tháng, tham gia các buổi công chiếu phim bom tấn độc quyền.")
                    .deadline(LocalDate.now().plusDays(19))
                    .status("ACTIVE")
                    .viewCount(180)
                    .createdAt(LocalDateTime.now().minusHours(13))
                    .build());

            // Tin 14
            jobPostingRepository.save(JobPosting.builder()
                    .title("Trợ Giảng Khóa Luyện Thi IELTS Sinh Viên Cần Thơ (VUS)")
                    .category(catTutor)
                    .employer(e6)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("Ca tối (18:30 - 21:00) Thứ 3, 5, 7")
                    .salaryRange("50.000đ - 70.000đ / giờ")
                    .description("Chấm bài Writing và Speaking cho sinh viên các trường ĐH Cần Thơ, giải đáp thắc mắc ngữ pháp.")
                    .requirements("Điểm IELTS tối thiểu 6.5 (không kỹ năng nào dưới 6.0), phát âm tiếng Anh chuẩn.")
                    .benefits("Được tiếp cận ngân hàng đề thi IELTS độc quyền của VUS, môi trường học thuật chuyên nghiệp.")
                    .deadline(LocalDate.now().plusDays(21))
                    .status("ACTIVE")
                    .viewCount(215)
                    .createdAt(LocalDateTime.now().minusHours(14))
                    .build());

            // Tin 15
            jobPostingRepository.save(JobPosting.builder()
                    .title("Nhân Viên Pha Chế Cà Phê Truyền Thống Ca Sáng (Highlands Hai Bà Trưng)")
                    .category(catParttime)
                    .employer(e1)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("06:30 - 11:30 (Đăng ký xoay ca theo tuần)")
                    .salaryRange("24.000đ - 28.000đ / giờ")
                    .description("Pha cà phê phin, freeze trà xanh, chuẩn bị nguyên vật liệu đầu ngày tại bờ kè bến Ninh Kiều.")
                    .requirements("Đúng giờ, có trách nhiệm, ưu tiên ứng viên có thể gắn bó từ 3 tháng trở lên.")
                    .benefits("Lương tháng 13 theo quy định chuỗi, thưởng sinh nhật, quà tết cho nhân viên.")
                    .deadline(LocalDate.now().plusDays(27))
                    .status("ACTIVE")
                    .viewCount(160)
                    .createdAt(LocalDateTime.now().minusHours(15))
                    .build());

            // Tin 16
            jobPostingRepository.save(JobPosting.builder()
                    .title("Nhân Viên Chăm Sóc Khách Hàng & Quầy Dịch Vụ (Lotte Mart An Hòa)")
                    .category(catRetail)
                    .employer(e7)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("Ca chiều: 14:00 - 18:30 hoặc Ca tối: 17:30 - 22:00")
                    .salaryRange("26.000đ - 30.000đ / giờ")
                    .description("Tiếp nhận ý kiến đóng góp của khách hàng, giải quyết đổi trả hàng hóa theo quy định Lotte.")
                    .requirements("Giọng nói nhẹ nhàng, giao tiếp tốt, bình tĩnh xử lý tình huống phát sinh.")
                    .benefits("Được đào tạo kỹ năng chăm sóc khách hàng chuẩn Hàn Quốc, cơ hội thăng tiến lên Trưởng ca.")
                    .deadline(LocalDate.now().plusDays(24))
                    .status("ACTIVE")
                    .viewCount(175)
                    .createdAt(LocalDateTime.now().minusHours(16))
                    .build());

            // Tin 17
            jobPostingRepository.save(JobPosting.builder()
                    .title("Thực Tập Sinh Lập Trình Mobile Flutter / Android Native (FPT Software)")
                    .category(catIntern)
                    .employer(e2)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Thực tập sinh (Internship)")
                    .shiftSchedule("Linh hoạt 3 - 4 ngày/tuần theo lịch báo cáo đồ án")
                    .salaryRange("5.000.000đ - 7.500.000đ / tháng")
                    .description("Tham gia phát triển các ứng dụng di động đa nền tảng với Flutter / Dart hoặc Android Kotlin.")
                    .requirements("Sinh viên CNTT nắm chắc kiến thức lập trình hướng đối tượng OOP và cấu trúc dữ liệu.")
                    .benefits("Được hướng dẫn trực tiếp từ Tech Lead dày dặn kinh nghiệm, hỗ trợ thiết bị test máy thật.")
                    .deadline(LocalDate.now().plusDays(36))
                    .status("ACTIVE")
                    .viewCount(310)
                    .createdAt(LocalDateTime.now().minusHours(17))
                    .build());

            // Tin 18
            jobPostingRepository.save(JobPosting.builder()
                    .title("Nhân Viên Thu Ngân Quầy Bánh Mì & Nước Uống Ca Sáng (Phúc Long Cần Thơ)")
                    .category(catParttime)
                    .employer(e4)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("07:00 - 11:30 (Thứ 2 đến Thứ 7)")
                    .salaryRange("24.000đ - 28.000đ / giờ")
                    .description("Nướng bánh mì, pha cà phê mang đi (Take-away) cho khách hàng công sở và sinh viên đi học buổi sáng.")
                    .requirements("Nhanh nhẹn, trung thực, có thể dậy sớm đi làm ca sáng.")
                    .benefits("Bao ăn sáng tại quán, phụ cấp gửi xe Vincom, chế độ thưởng lễ tết hấp dẫn.")
                    .deadline(LocalDate.now().plusDays(17))
                    .status("ACTIVE")
                    .viewCount(152)
                    .createdAt(LocalDateTime.now().minusHours(18))
                    .build());

            // Tin 19
            jobPostingRepository.save(JobPosting.builder()
                    .title("Gia Sư Dạy Kèm Tiếng Anh Giao Tiếp Cho Người Đi Làm (Ninh Kiều)")
                    .category(catTutor)
                    .employer(e6)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("19:00 - 21:00 (Thứ 3 và Thứ 5)")
                    .salaryRange("120.000đ - 150.000đ / buổi (90 phút)")
                    .description("Kèm 1-1 kỹ năng phát âm và giao tiếp đàm phán cơ bản cho nhân viên văn phòng tại Cần Thơ.")
                    .requirements("Sinh viên năm cuối ngành Ngôn Ngữ Anh hoặc có chứng chỉ quốc tế, năng nổ và kiên nhẫn.")
                    .benefits("Thu nhập cao, linh hoạt địa điểm học tại quán cà phê yên tĩnh hoặc tại nhà học viên.")
                    .deadline(LocalDate.now().plusDays(14))
                    .status("ACTIVE")
                    .viewCount(188)
                    .createdAt(LocalDateTime.now().minusHours(19))
                    .build());

            // Tin 20
            jobPostingRepository.save(JobPosting.builder()
                    .title("Nhân Viên Bán Hàng & Tiếp Thị Nước Giải Khát Ca Chiều (GO! Cái Răng)")
                    .category(catRetail)
                    .employer(e3)
                    .location("Quận Cái Răng, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("13:30 - 18:00 (Linh hoạt 4-6 ca/tuần)")
                    .salaryRange("25.000đ - 30.000đ / giờ")
                    .description("Mời khách dùng thử sản phẩm nước giải khát mới, tư vấn chương trình khuyến mãi giảm giá tại siêu thị.")
                    .requirements("Giao tiếp hoạt bát, tươi cười, không nói ngọng, có sức khỏe tốt.")
                    .benefits("Thưởng hoa hồng theo số lượng sản phẩm bán ra, nhận quà tặng của hãng định kỳ.")
                    .deadline(LocalDate.now().plusDays(23))
                    .status("ACTIVE")
                    .viewCount(165)
                    .createdAt(LocalDateTime.now().minusHours(20))
                    .build());

            // Tin 21 (Bắt đầu trang 3)
            jobPostingRepository.save(JobPosting.builder()
                    .title("Thực Tập Sinh Hỗ Trợ Kỹ Thuật IT Helpdesk & Quản Trị Mạng (FPT Cần Thơ)")
                    .category(catIntern)
                    .employer(e2)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Thực tập sinh (Internship)")
                    .shiftSchedule("08:00 - 12:00 hoặc 13:30 - 17:30 (Thứ 2 - Thứ 6)")
                    .salaryRange("4.500.000đ - 6.500.000đ / tháng")
                    .description("Cài đặt hệ điều hành, cấu hình mạng LAN/Wifi văn phòng, hỗ trợ nhân sự xử lý sự cố máy tính.")
                    .requirements("Sinh viên ngành Mạng máy tính hoặc CNTT, biết lắp ráp phần cứng máy tính PC.")
                    .benefits("Làm việc trong môi trường công nghệ hiện đại, cơ hội chuyển tiếp thành nhân viên chính thức.")
                    .deadline(LocalDate.now().plusDays(34))
                    .status("ACTIVE")
                    .viewCount(260)
                    .createdAt(LocalDateTime.now().minusHours(21))
                    .build());

            // Tin 22
            jobPostingRepository.save(JobPosting.builder()
                    .title("Nhân Viên Pha Chế & Phục Vụ Ca Đêm 21:00 - 02:00 (Highlands Cần Thơ)")
                    .category(catParttime)
                    .employer(e1)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("Ca đêm (21:00 - 02:00 sáng hôm sau)")
                    .salaryRange("30.000đ - 38.000đ / giờ (Phụ cấp ca đêm cao)")
                    .description("Phục vụ khách đêm tại khu vực trung tâm bến Ninh Kiều, dọn dẹp và chốt ca cuối ngày.")
                    .requirements("Nam/Nữ từ 18 tuổi trở lên, có sức khỏe làm ca đêm, có phương tiện đi lại an toàn.")
                    .benefits("Phụ cấp ăn đêm 30.000đ/ca, tiền tip chia đều hàng tuần, hỗ trợ tiền xăng xe.")
                    .deadline(LocalDate.now().plusDays(25))
                    .status("ACTIVE")
                    .viewCount(210)
                    .createdAt(LocalDateTime.now().minusHours(22))
                    .build());

            // Tin 23
            jobPostingRepository.save(JobPosting.builder()
                    .title("Nhân Viên Kỹ Thuật Máy Chiếu & Vận Hành Phòng Chiếu Phim (CGV)")
                    .category(catRetail)
                    .employer(e5)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("Ca linh hoạt theo lịch chiếu phim rạp (4 tiếng/ca)")
                    .salaryRange("25.000đ - 29.000đ / giờ")
                    .description("Kiểm tra danh sách suất chiếu phim, vận hành hệ thống âm thanh vòm Dolby Atmos và máy chiếu kỹ thuật số.")
                    .requirements("Sinh viên Điện tử, CNTT hoặc đam mê công nghệ nghe nhìn, cẩn thận và ham học hỏi.")
                    .benefits("Được chuyên gia kỹ thuật CGV đào tạo chuyển giao công nghệ, miễn phí xem phim không giới hạn.")
                    .deadline(LocalDate.now().plusDays(31))
                    .status("ACTIVE")
                    .viewCount(225)
                    .createdAt(LocalDateTime.now().minusHours(23))
                    .build());

            // Tin 24
            jobPostingRepository.save(JobPosting.builder()
                    .title("Cộng Tác Viên Soạn Tài Liệu & Đề Thi Tiếng Anh Tiểu Học (VUS Cần Thơ)")
                    .category(catTutor)
                    .employer(e6)
                    .location("Quận Ninh Kiều, TP. Cần Thơ")
                    .jobType("Part-time (Bán thời gian)")
                    .shiftSchedule("Làm việc linh hoạt tại nhà hoặc văn phòng (15-20 giờ/tuần)")
                    .salaryRange("3.000.000đ - 4.500.000đ / tháng")
                    .description("Biên soạn bài tập, thiết kế slide bài giảng tương tác bằng Canva / PowerPoint cho giáo viên bản ngữ.")
                    .requirements("Thành thạo tin học văn phòng, có gu thẩm mỹ tốt, tiếng Anh đọc viết khá.")
                    .benefits("Chủ động thời gian làm việc không gò bó, phù hợp làm thêm trong ký túc xá sinh viên.")
                    .deadline(LocalDate.now().plusDays(29))
                    .status("ACTIVE")
                    .viewCount(170)
                    .createdAt(LocalDateTime.now().minusHours(24))
                    .build());
        }

        // 6. Doanh nghiệp Chờ Duyệt (PENDING) cho Super Admin duyệt
        if (employerRepository.findByCompanyNameIgnoreCase("Hệ Thống Trà Sữa TocoToco Cần Thơ").isEmpty()) {
            User empUserPending = userRepository.findByUsername("tocotoco_cantho").orElseGet(() -> userRepository.save(User.builder()
                    .username("tocotoco_cantho")
                    .password("123456")
                    .fullName("Quản Lý Tuyển Dụng TocoToco Ninh Kiều")
                    .email("hr.cantho@tocotocotea.com")
                    .phone("02923777666")
                    .role(Role.COMPANY_ADMIN)
                    .active(true)
                    .build()));
            employerRepository.save(Employer.builder()
                    .companyName("Hệ Thống Trà Sữa TocoToco Cần Thơ")
                    .address("Số 55 Đường 30 Tháng 4, Phường An Lạc, Quận Ninh Kiều, TP. Cần Thơ")
                    .website("https://tocotocotea.com")
                    .description("Thương hiệu trà sữa thuần Việt, cần tuyển 15 bạn sinh viên làm thêm ca xoay linh hoạt.")
                    .contactEmail("recruitment@tocotocotea.com")
                    .contactPhone("02923777666")
                    .status("PENDING")
                    .user(empUserPending)
                    .build());
        }

        // 7. Doanh nghiệp Bị Khóa 1-click (LOCKED)
        if (employerRepository.findByCompanyNameIgnoreCase("Công Ty Đầu Tư Tài Chính Toàn Cầu SkyInvest").isEmpty()) {
            User empUserLocked = userRepository.findByUsername("skyinvest_cantho").orElseGet(() -> userRepository.save(User.builder()
                    .username("skyinvest_cantho")
                    .password("123456")
                    .fullName("Đại Diện SkyInvest Chi Nhánh Cần Thơ")
                    .email("contact@skyinvest.fake")
                    .phone("0900999888")
                    .role(Role.COMPANY_ADMIN)
                    .active(false)
                    .build()));
            employerRepository.save(Employer.builder()
                    .companyName("Công Ty Đầu Tư Tài Chính Toàn Cầu SkyInvest")
                    .address("Đường Nguyễn Văn Cừ, Phường An Khánh, Quận Ninh Kiều, TP. Cần Thơ")
                    .website("https://skyinvest-fake.com")
                    .description("Tài khoản đã bị Super Admin kích hoạt chế tài KHÓA 1-CLICK do có dấu hiệu lừa đảo thu tiền cọc đồng phục và đa cấp đối với sinh viên.")
                    .contactEmail("contact@skyinvest.fake")
                    .contactPhone("0900999888")
                    .status("LOCKED")
                    .user(empUserLocked)
                    .build());
        }

        // 8. Đánh giá & Chấm sao mẫu từ sinh viên Cần Thơ
        if (companyReviewRepository.count() == 0) {
            Employer highlands = employerRepository.findByCompanyNameIgnoreCase("Highlands Coffee Bến Ninh Kiều").orElse(null);
            User student = userRepository.findByUsername("sinhvien_cantho").orElse(null);
            if (highlands != null && student != null) {
                companyReviewRepository.save(com.uniwork.entity.CompanyReview.builder()
                        .employer(highlands)
                        .student(student)
                        .studentName("Nguyễn Văn Sinh Viên (FPT Poly Cần Thơ)")
                        .rating(5)
                        .title("Quán trả lương cực kỳ đúng hạn, quản lý rất thương sinh viên")
                        .content("Mình làm ca tối 17:30 - 22:30 tại Bến Ninh Kiều được 6 tháng. Lương ngày 5 hàng tháng ting ting đúng ngày. Anh quản lý luôn sắp ca tránh lịch thi giữa kỳ của mình.")
                        .salaryRating("Đúng hạn")
                        .environmentRating("Thân thiện hỗ trợ")
                        .verifiedStudent(true)
                        .build());

                companyReviewRepository.save(com.uniwork.entity.CompanyReview.builder()
                        .employer(highlands)
                        .student(student)
                        .studentName("Lê Hoàng Nam (ĐH Cần Thơ - K47)")
                        .rating(5)
                        .title("Môi trường pha chế chuyên nghiệp, được giảm 50% menu")
                        .content("Được học công thức pha Phin Sữa Đá chuẩn chuỗi. Nước uống được giảm giá 50% cho nhân viên. Rất khuyến khích các bạn sinh viên năm 1-2 ứng tuyển thử sức.")
                        .salaryRating("Đúng hạn")
                        .environmentRating("Chuyên nghiệp")
                        .verifiedStudent(true)
                        .build());
            }
        }

        log.info("Khởi tạo dữ liệu mẫu UniWork Cần Thơ hoàn tất thành công!");
    }
}
