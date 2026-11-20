-- =========================================================================
-- HỆ THỐNG CƠ SỞ DỮ LIỆU UNIWORK CẦN THƠ (UniWorkDB)
-- Nền tảng việc làm & thực tập cho sinh viên tại TP. Cần Thơ
-- Phục vụ đồ án môn Lập trình Java 5 - FPT Polytechnic Cần Thơ
-- =========================================================================

-- 1. TẠO CƠ SỞ DỮ LIỆU
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'UniWorkDB')
BEGIN
    CREATE DATABASE UniWorkDB COLLATE Vietnamese_CI_AS;
END
GO

USE UniWorkDB;
GO

-- 2. XÓA BẢNG CŨ (NẾU CẦN RESET)
IF OBJECT_ID('dbo.job_applications', 'U') IS NOT NULL DROP TABLE dbo.job_applications;
IF OBJECT_ID('dbo.job_postings', 'U') IS NOT NULL DROP TABLE dbo.job_postings;
IF OBJECT_ID('dbo.employer_reviews', 'U') IS NOT NULL DROP TABLE dbo.employer_reviews;
IF OBJECT_ID('dbo.employers', 'U') IS NOT NULL DROP TABLE dbo.employers;
IF OBJECT_ID('dbo.job_categories', 'U') IS NOT NULL DROP TABLE dbo.job_categories;
IF OBJECT_ID('dbo.users', 'U') IS NOT NULL DROP TABLE dbo.users;
GO

-- 3. BẢNG TÀI KHOẢN NGƯỜI DÙNG (users)
-- Gồm: SUPER_ADMIN (Admin tối cao), COMPANY_ADMIN (Admin đại diện công ty), STUDENT (Sinh viên)
CREATE TABLE dbo.users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL, -- SUPER_ADMIN, COMPANY_ADMIN, STUDENT
    avatar VARCHAR(255),
    active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE()
);
GO

-- 4. BẢNG NHÀ TUYỂN DỤNG / DOANH NGHIỆP TẠI CẦN THƠ (employers)
-- Mỗi Company Admin đại diện cho 1 công ty
CREATE TABLE dbo.employers (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    company_name NVARCHAR(150) NOT NULL,
    company_logo VARCHAR(255),
    website VARCHAR(150),
    address NVARCHAR(255) NOT NULL, -- Địa chỉ chi nhánh tại TP. Cần Thơ (Ninh Kiều, Cái Răng...)
    description NVARCHAR(MAX),
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    user_id BIGINT UNIQUE NOT NULL, -- Khóa ngoại liên kết tới Company Admin
    CONSTRAINT FK_Employer_User FOREIGN KEY (user_id) REFERENCES dbo.users(id) ON DELETE CASCADE
);
GO

-- 5. BẢNG DANH MỤC NGÀNH NGHỀ (job_categories)
CREATE TABLE dbo.job_categories (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    icon VARCHAR(50),
    description NVARCHAR(255),
    active BIT NOT NULL DEFAULT 1
);
GO

-- 6. BẢNG BÀI ĐĂNG TUYỂN DỤNG (job_postings)
-- Gắn liền với ca làm việc sinh viên và khu vực quận/huyện Cần Thơ
CREATE TABLE dbo.job_postings (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX) NOT NULL,
    requirements NVARCHAR(MAX),
    benefits NVARCHAR(MAX),
    salary_range NVARCHAR(100), -- VD: 25.000đ - 30.000đ / giờ
    location NVARCHAR(200) NOT NULL, -- Ninh Kiều, Cái Răng, Bình Thủy...
    job_type NVARCHAR(50), -- Part-time, Full-time, Internship
    shift_schedule NVARCHAR(150), -- Ca sáng (7:00-11:30), Ca tối (17:30-22:30)
    deadline DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- PENDING, ACTIVE, CLOSED
    view_count INT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    employer_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    CONSTRAINT FK_Job_Employer FOREIGN KEY (employer_id) REFERENCES dbo.employers(id) ON DELETE CASCADE,
    CONSTRAINT FK_Job_Category FOREIGN KEY (category_id) REFERENCES dbo.job_categories(id)
);
GO

-- 7. BẢNG ĐƠN ỨNG TUYỂN CỦA SINH VIÊN (job_applications)
CREATE TABLE dbo.job_applications (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    student_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    cv_url VARCHAR(255),
    cover_letter NVARCHAR(MAX), -- Nguyện vọng, đăng ký ca rảnh
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, REVIEWING, ACCEPTED, REJECTED
    applied_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_App_Student FOREIGN KEY (student_id) REFERENCES dbo.users(id),
    CONSTRAINT FK_App_Job FOREIGN KEY (job_id) REFERENCES dbo.job_postings(id) ON DELETE CASCADE
);
GO

-- 8. BẢNG ĐÁNH GIÁ CÔNG TY (employer_reviews)
-- Sinh viên review độ uy tín của nơi làm việc
CREATE TABLE dbo.employer_reviews (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    employer_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment NVARCHAR(1000),
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Review_Employer FOREIGN KEY (employer_id) REFERENCES dbo.employers(id),
    CONSTRAINT FK_Review_Student FOREIGN KEY (student_id) REFERENCES dbo.users(id)
);
GO

-- =========================================================================
-- 9. DỮ LIỆU MẪU BAN ĐẦU (SEED DATA - CẦN THƠ)
-- =========================================================================

-- 9.1. Danh mục ngành nghề
INSERT INTO dbo.job_categories (name, code, icon, description, active) VALUES
(N'Phục Vụ & Pha Chế Cà Phê / Trà Sữa', 'CAFE_BARISTA', 'bi-cup-hot', N'Phù hợp làm thêm ca linh hoạt theo lịch học sinh viên Cần Thơ', 1),
(N'Thực Tập Sinh CNTT & Kỹ Thuật (Internship)', 'TECH_INTERNSHIP', 'bi-laptop', N'Dành cho sinh viên CNTT các trường ĐH Cần Thơ, FPT Polytechnic...', 1),
(N'Bán Hàng & Thu Ngân Siêu Thị / Shop', 'RETAIL', 'bi-bag-check', N'Làm việc tại các trung tâm thương mại Vincom, Lotte Mart, GO! Cần Thơ', 1),
(N'Gia Sư & Trợ Giảng Trung Tâm', 'TUTOR', 'bi-book', N'Dành cho sinh viên có thế mạnh tiếng Anh, Toán, Lập trình căn bản', 1);

-- 9.2. Tài khoản người dùng (Password mẫu: 123456)
INSERT INTO dbo.users (username, password, full_name, email, phone, role, active) VALUES
-- Super Admin điều hành hệ thống
('superadmin', '123456', N'Quản Trị Viên Tối Cao UniWork', 'superadmin@uniwork.edu.vn', '0912345678', 'SUPER_ADMIN', 1),

-- Sinh viên mẫu
('sinhvien_cantho', '123456', N'Nguyễn Văn Sinh Viên (FPT Poly Cần Thơ)', 'sinhvienct@fpt.edu.vn', '0939123456', 'STUDENT', 1),

-- Các Company Admin đại diện cho các doanh nghiệp tại TP. Cần Thơ
('highlandscantho', '123456', N'Admin Highlands Coffee Bến Ninh Kiều', 'ninhkieu@highlandscoffee.com.vn', '02923888999', 'COMPANY_ADMIN', 1),
('fptsoftware_cantho', '123456', N'Admin Tuyển Dụng FPT Software Cần Thơ', 'tuyendung.cantho@fpt.com', '02923777888', 'COMPANY_ADMIN', 1),
('gocantho', '123456', N'Admin Siêu Thị GO! Cần Thơ', 'hr@go-cantho.vn', '02923666777', 'COMPANY_ADMIN', 1);

-- 9.3. Thông tin Doanh nghiệp / Cửa hàng tại Cần Thơ
DECLARE @uHighlands BIGINT = (SELECT id FROM dbo.users WHERE username = 'highlandscantho');
DECLARE @uFpt BIGINT = (SELECT id FROM dbo.users WHERE username = 'fptsoftware_cantho');
DECLARE @uGo BIGINT = (SELECT id FROM dbo.users WHERE username = 'gocantho');

INSERT INTO dbo.employers (company_name, company_logo, website, address, description, contact_email, contact_phone, user_id) VALUES
(N'Highlands Coffee Bến Ninh Kiều', 'https://upload.wikimedia.org/wikipedia/vi/thumb/b/b3/Highlands_Coffee_logo.svg/1200px-Highlands_Coffee_logo.svg.png', 'https://highlandscoffee.com.vn', N'Đường Hai Bà Trưng, Phường Tân An, Quận Ninh Kiều, TP. Cần Thơ', N'Chi nhánh Highlands bờ kè Bến Ninh Kiều chuyên tuyển sinh viên part-time làm việc theo ca linh hoạt.', 'hr.cantho@highlandscoffee.com.vn', '02923888999', @uHighlands),
(N'FPT Software Cần Thơ', 'https://fptsoftware.com/assets/images/logo.svg', 'https://fptsoftware.com', N'Đường 30/4, Phường Xuân Khánh, Quận Ninh Kiều, TP. Cần Thơ', N'Văn phòng công nghệ phần mềm FPT tại Cần Thơ với các chương trình Fresher/Internship chuyên nghiệp.', 'fresher.cantho@fpt.com', '02923777888', @uFpt),
(N'Đại Siêu Thị GO! Cần Thơ (Big C cũ)', 'https://go-vietnam.vn/assets/img/logo.png', 'https://go-vietnam.vn', N'Lô số 1, KDC Hưng Phú 1, Phường Hưng Phú, Quận Cái Răng, TP. Cần Thơ', N'Trung tâm mua sắm và siêu thị lớn hàng đầu khu vực Tây Nam Bộ tại Quận Cái Răng.', 'tuyendung@go-cantho.vn', '02923666777', @uGo);

-- 9.4. Việc làm mẫu tại Cần Thơ
DECLARE @eHighlands BIGINT = (SELECT id FROM dbo.employers WHERE company_name LIKE N'%Highlands%');
DECLARE @eFpt BIGINT = (SELECT id FROM dbo.employers WHERE company_name LIKE N'%FPT Software%');
DECLARE @eGo BIGINT = (SELECT id FROM dbo.employers WHERE company_name LIKE N'%GO!%');

DECLARE @cCafe BIGINT = (SELECT id FROM dbo.job_categories WHERE code = 'CAFE_BARISTA');
DECLARE @cTech BIGINT = (SELECT id FROM dbo.job_categories WHERE code = 'TECH_INTERNSHIP');
DECLARE @cRetail BIGINT = (SELECT id FROM dbo.job_categories WHERE code = 'RETAIL');

INSERT INTO dbo.job_postings (title, description, requirements, benefits, salary_range, location, job_type, shift_schedule, deadline, status, view_count, employer_id, category_id) VALUES
(N'Nhân Viên Pha Chế / Phục Vụ Ca Xoay Sinh Viên (Bến Ninh Kiều)', 
 N'Pha chế thức uống theo công thức, phục vụ khách tham quan Bến Ninh Kiều, dọn dẹp khu vực quầy bar.', 
 N'Sinh viên các trường ĐH Cần Thơ, FPT Polytechnic, Nam Cần Thơ... Thân thiện, chăm chỉ, không yêu cầu kinh nghiệm.', 
 N'Được đào tạo pha chế bài bản, giảm 50% đồ uống toàn chuỗi, hỗ trợ dấu mộc thực tập tốt nghiệp.', 
 N'23.000đ - 28.000đ / giờ + Thưởng', 
 N'Quận Ninh Kiều, TP. Cần Thơ', 
 N'Part-time (Ca 4–5 tiếng)', 
 N'Ca sáng (07:00 - 11:30), Ca tối (17:30 - 22:30), đăng ký theo tuần', 
 DATEADD(day, 30, GETDATE()), 'ACTIVE', 168, @eHighlands, @cCafe),

(N'Thực Tập Sinh Java / Spring Boot Backend (Trợ Cấp 5–8 Triệu)', 
 N'Tham gia phát triển dự án công nghệ thực tế cùng Mentor, học hỏi Spring Boot, RESTful API và Hibernate SQL Server.', 
 N'Sinh viên năm 3, 4 hoặc mới tốt nghiệp ngành CNTT các trường tại Cần Thơ. Nắm chắc OOP và cơ sở dữ liệu.', 
 N'Trợ cấp thực tập hàng tháng từ 5-8 triệu, lộ trình đào tạo rõ ràng, cơ hội ký hợp đồng chính thức sau 3 tháng.', 
 N'5.000.000đ - 8.000.000đ / tháng', 
 N'Quận Ninh Kiều, TP. Cần Thơ', 
 N'Thực tập sinh (Internship)', 
 N'Linh hoạt 3–4 ngày/tuần theo thời khóa biểu học kỳ', 
 DATEADD(day, 45, GETDATE()), 'ACTIVE', 320, @eFpt, @cTech),

(N'Thu Ngân & Kiểm Kê Hàng Hóa Ca Tối / Cuối Tuần (GO! Cái Răng)', 
 N'Thực hiện thanh toán quét mã vạch cho khách hàng, hỗ trợ xếp kệ hàng và kiểm kê hàng hóa cuối ngày.', 
 N'Trung thực, nhanh nhẹn, ưu tiên sinh viên cư trú tại khu vực Quận Cái Răng hoặc gần cầu Hưng Lợi.', 
 N'Phụ cấp ăn ca, thưởng lễ tết theo quy định, môi trường làm việc máy lạnh mát mẻ.', 
 N'25.000đ - 30.000đ / giờ', 
 N'Quận Cái Răng, TP. Cần Thơ', 
 N'Part-time (Ca linh hoạt)', 
 N'Ca tối (18:00 - 22:00) hoặc Thứ Bảy, Chủ Nhật', 
 DATEADD(day, 25, GETDATE()), 'ACTIVE', 194, @eGo, @cRetail);
GO

-- THÔNG BÁO HOÀN TẤT
PRINT N'>>> ĐÃ KHỞI TẠO CƠ SỞ DỮ LIỆU UniWorkDB CHO CẦN THƠ THÀNH CÔNG! <<<';
GO
