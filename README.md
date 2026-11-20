# UniWork - Nền Tảng Tìm Việc Làm & Thực Tập Cho Sinh Viên
> **Dự án Chuẩn Spring Boot MVC & Thymeleaf theo Slide bài giảng Java 5 - FPT Polytechnic**

---

## 1. Giới thiệu dự án
**UniWork** (viết tắt ghép từ **Uni**versity & **Work**) là nền tảng công nghệ việc làm chuyên biệt kết nối sinh viên các trường Đại học & Cao đẳng với các nhà tuyển dụng uy tín. Hệ thống giải quyết trực tiếp bài toán tìm việc làm thêm (part-time theo ca linh hoạt), công việc freelance từ xa (remote) và cơ hội thực tập doanh nghiệp (internship) có hỗ trợ phụ cấp cho sinh viên.

### Các tính năng cốt lõi:
- **Trang chủ tìm kiếm thông minh**: Lọc việc làm theo ngành nghề, từ khóa và hình thức làm việc.
- **Việc làm ca linh hoạt**: Doanh nghiệp công khai lịch ca sáng/chiều/tối để sinh viên đăng ký khớp với thời khóa biểu học tập.
- **Nộp hồ sơ ứng tuyển trực tuyến**: Sinh viên gửi thông tin, ghi chú lịch rảnh và CV online trực tiếp cho nhà tuyển dụng.
- **Trang Demo Hello World**: Minh họa chuẩn Slide 1.1 (`/poly/hello`).
- **REST API**: Cung cấp API (`/api/jobs`) phục vụ tra cứu dữ liệu dạng JSON.

---

## 2. Công nghệ & Kiến trúc Hệ thống (Chuẩn Slide 1.1, 1.2, 2.1, 2.2)
- **Ngôn ngữ**: Java 21 LTS
- **Framework**: Spring Boot 3.2.5 (Spring MVC, Spring Data JPA)
- **Template Engine**: **Thymeleaf** (`src/main/resources/templates/`), Bootstrap 5, Bootstrap Icons
- **Thư mục tĩnh**: `src/main/resources/static/` (CSS, JS, Images)
- **Đóng gói**: Đóng gói WAR (`<packaging>war</packaging>` và `ServletInitializer.java`)
- **Cơ sở dữ liệu**: Microsoft SQL Server (`UniWorkDB`) qua Hibernate ORM
- **Thư viện hỗ trợ**: Project Lombok (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- **Kiểm thử tự động**: JUnit 5 (Jupiter), Mockito, `maven-surefire-plugin`

---

## 3. Cấu trúc thư mục (Chuẩn Slide 1.1 FPT Polytechnic)
```text
UniWork/
├── src/
│   ├── main/
│   │   ├── java/com/uniwork/
│   │   │   ├── config/          # Cấu hình hệ thống & Khởi tạo dữ liệu mẫu (DataInitializer)
│   │   │   ├── controller/      # Web Controllers (Thymeleaf) & REST API Controllers
│   │   │   ├── dto/             # Data Transfer Objects & ApiResponse
│   │   │   ├── entity/          # JPA Entities (User, Employer, JobPosting, JobCategory, JobApplication)
│   │   │   ├── repository/      # Spring Data JPA Repositories
│   │   │   ├── service/         # Interfaces & Implementations nghiệp vụ
│   │   │   │   └── impl/
│   │   │   ├── exception/       # Xử lý ngoại lệ tập trung (ResourceNotFoundException, GlobalExceptionHandler)
│   │   │   ├── ServletInitializer.java     # Cấu hình WAR deployment (Slide 1.1 Trang 15)
│   │   │   └── UniWorkApplication.java     # File khởi chạy ứng dụng (Slide 1.1 Trang 15)
│   │   └── resources/
│   │       ├── static/          # Thư mục tĩnh CSS, JS (Slide 1.1 Trang 15)
│   │       │   └── css/style.css
│   │       ├── templates/       # Thư mục chứa template Thymeleaf (Slide 1.1 Trang 15, 22)
│   │       │   ├── common/      # header.html, footer.html (Fragments)
│   │       │   ├── demo/        # hello.html (Trang Demo Slide 1.1 Trang 23)
│   │       │   ├── jobs/        # list.html, detail.html
│   │       │   ├── index.html   # Trang chủ tìm việc sinh viên UniWork
│   │       │   └── error.html   # Trang báo lỗi
│   │       └── application.properties # Cấu hình Thymeleaf, CSDL SQL Server UniWorkDB, port
│   └── test/
│       ├── java/com/uniwork/    # Thư mục kiểm thử JUnit 5 & Mockito
│       └── resources/           # Cấu hình H2 In-Memory cho kiểm thử tự động
├── pom.xml                      # Cấu hình Maven dependencies & plugins
└── README.md                    # Tài liệu hướng dẫn dự án UniWork
```

---

## 4. Hướng dẫn cài đặt & Khởi chạy

### Bước 1: Khởi động CSDL SQL Server
- Mở SQL Server Management Studio (SSMS) và tạo CSDL:
  ```sql
  CREATE DATABASE UniWorkDB;
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
- **Trang chủ UniWork**: `http://localhost:8080/`
- **Trang Demo Slide 1.1**: `http://localhost:8080/poly/hello`
- **Danh sách việc làm**: `http://localhost:8080/jobs`
- **REST API việc làm**: `http://localhost:8080/api/jobs`
