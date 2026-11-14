# StudentJobPortal - Nền Tảng Tìm Việc Làm & Thực Tập Cho Sinh Viên
> **Dự án Môn học Java 5 / Spring Boot WebApp - FPT Polytechnic**

---

## 1. Giới thiệu dự án
**StudentJobPortal** là hệ thống việc làm chuyên biệt kết nối sinh viên các trường Đại học & Cao đẳng với các nhà tuyển dụng uy tín. Hệ thống giải quyết trực tiếp bài toán tìm việc làm thêm (part-time theo ca linh hoạt), công việc freelance từ xa (remote) và cơ hội thực tập doanh nghiệp (internship) có hỗ trợ phụ cấp cho sinh viên.

### Các tính năng cốt lõi:
- **Trang chủ tìm kiếm thông minh**: Lọc việc làm theo ngành nghề, từ khóa và hình thức làm việc.
- **Việc làm ca linh hoạt**: Doanh nghiệp công khai lịch ca sáng/chiều/tối để sinh viên đăng ký khớp với thời khóa biểu học tập.
- **Nộp hồ sơ ứng tuyển trực tuyến**: Sinh viên gửi thông tin, ghi chú lịch rảnh và CV online trực tiếp cho nhà tuyển dụng.
- **Doanh nghiệp uy tín**: Hệ thống phân loại doanh nghiệp rõ ràng, ngăn ngừa bẫy lừa đảo/đa cấp.
- **REST API**: Cung cấp API (`/api/jobs`) phục vụ tra cứu dữ liệu dạng JSON.

---

## 2. Công nghệ & Kiến trúc Hệ thống
- **Ngôn ngữ**: Java 21 LTS
- **Framework**: Spring Boot 3.2.5 (Spring MVC, Spring Data JPA)
- **Giao diện Web**: WebApp JSP & JSTL (`src/main/webapp/WEB-INF/views/`), Bootstrap 5, Bootstrap Icons
- **Đóng gói**: Đóng gói dạng WAR (`<packaging>war</packaging>` và `SpringBootServletInitializer`)
- **Cơ sở dữ liệu**: Microsoft SQL Server (`StudentJobDB`) qua Hibernate ORM
- **Thư viện hỗ trợ**: Project Lombok (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- **Kiểm thử tự động**: JUnit 5 (Jupiter), Mockito, `maven-surefire-plugin`

---

## 3. Cấu trúc thư mục (Phân tầng Chuẩn Java 5)
```text
StudentJobPortal/
├── src/
│   ├── main/
│   │   ├── java/com/studentjob/
│   │   │   ├── config/          # Cấu hình hệ thống & Khởi tạo dữ liệu mẫu (DataInitializer)
│   │   │   ├── controller/      # Web Controllers (JSP) & REST API Controllers
│   │   │   ├── dto/             # Data Transfer Objects & ApiResponse
│   │   │   ├── entity/          # JPA Entities (User, Employer, JobPosting, JobCategory, JobApplication)
│   │   │   ├── repository/      # Spring Data JPA Repositories
│   │   │   ├── service/         # Interfaces & Implementations nghiệp vụ
│   │   │   │   └── impl/
│   │   │   ├── exception/       # Xử lý ngoại lệ tập trung (ResourceNotFoundException, GlobalExceptionHandler)
│   │   │   └── StudentJobApplication.java  # File khởi chạy ứng dụng (kế thừa SpringBootServletInitializer)
│   │   ├── resources/
│   │   │   └── application.properties       # Cấu hình CSDL SQL Server, port, view resolver
│   │   └── webapp/
│   │       ├── WEB-INF/views/   # Giao diện JSP & JSTL (common header/footer, index, jobs)
│   │       └── assets/          # Thư mục tĩnh (CSS, JS, hình ảnh)
│   └── test/
│       ├── java/com/studentjob/ # Thư mục kiểm thử JUnit 5 & Mockito
│       └── resources/           # Cấu hình H2 In-Memory cho kiểm thử tự động độc lập
├── pom.xml                      # Cấu hình Maven dependencies & plugins
└── README.md                    # Tài liệu hướng dẫn dự án
```

---

## 4. Hướng dẫn cài đặt & Khởi chạy

### Bước 1: Khởi động CSDL SQL Server
- Mở SQL Server Management Studio (SSMS) và tạo CSDL:
  ```sql
  CREATE DATABASE StudentJobDB;
  ```
- Kiểm tra thông tin tài khoản `sa` và `password` trong file `src/main/resources/application.properties`.

### Bước 2: Chạy kiểm thử tự động
```bash
mvn clean test
```

### Bước 3: Khởi chạy ứng dụng WebApp
```bash
mvn spring-boot:run
```

### Bước 4: Trải nghiệm ứng dụng
- Mở trình duyệt web truy cập: `http://localhost:8080/`
- Hệ thống sẽ tự động khởi tạo dữ liệu danh mục và tin tuyển dụng mẫu sinh viên (Highlands Coffee, FPT Software...) thông qua `DataInitializer`.
