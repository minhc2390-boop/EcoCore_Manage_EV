# EcoCore_Manage_EV
> **Hệ thống Đặt chỗ & Quản lý Điểm sạc Xe điện Công cộng (EVCSMS)**

## 1. Công nghệ & Kiến trúc
* **Framework**: Spring Boot 3.2.5 (Spring MVC, Spring Data JPA)
* **Java Version**: Java 21 LTS
* **Giao diện Web**: WebApp JSP & JSTL (`src/main/webapp/WEB-INF/views/`), Bootstrap 5
* **CSDL**: Microsoft SQL Server
* **Thư viện**: Project Lombok
* **Kiểm thử**: JUnit 5 (Jupiter), Mockito

## 2. Cấu trúc thư mục (Layered Architecture)
```text
src/
├── main/
│   ├── java/com/evcsms/
│   │   ├── config/          # Cấu hình hệ thống (CORS, Security, v.v.)
│   │   ├── controller/      # Web Controllers & REST Controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA Entities (Model bảng CSDL)
│   │   ├── repository/      # Spring Data JPA Repositories
│   │   ├── service/         # Interfaces & Implementations nghiệp vụ
│   │   │   └── impl/
│   │   ├── exception/       # Xử lý ngoại lệ tập trung
│   │   └── DaEvcsmsApplication.java  # File khởi chạy ứng dụng
│   ├── resources/
│   │   └── application.properties    # Cấu hình CSDL, port, view resolver
│   └── webapp/
│       ├── WEB-INF/views/   # Giao diện JSP (common header/footer, pages)
│       └── assets/          # CSS, JavaScript, hình ảnh tĩnh
└── test/
    └── java/com/evcsms/     # Thư mục kiểm thử JUnit 5
```

## 3. Hướng dẫn chạy dự án
1. **Clone dự án:**
   ```bash
   git clone https://github.com/minhc2390-boop/EcoCore_Manage_EV.git
   cd EcoCore_Manage_EV
   ```
2. **Cấu hình CSDL:**
   * Mở file `src/main/resources/application.properties`
   * Kiểm tra thông tin `username` và `password` SQL Server máy của bạn.
3. **Chạy ứng dụng:**
   ```bash
   mvn spring-boot:run
   ```
   * Mở trình duyệt: `http://localhost:8080/`
