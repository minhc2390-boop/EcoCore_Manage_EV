package com.studentjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Lớp khởi chạy ứng dụng Website Tìm Việc Cho Sinh Viên (StudentJobPortal)
 * Kế thừa SpringBootServletInitializer để hỗ trợ đóng gói định dạng WAR (Chuẩn Java 5 WebApp)
 */
@SpringBootApplication
public class StudentJobApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(StudentJobApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(StudentJobApplication.class, args);
    }
}
