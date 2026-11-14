package com.studentjob;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Cấu hình triển khai WAR trên Web Server bên ngoài (Tomcat)
 * Chuẩn cấu trúc dự án Spring Boot theo Slide 1.1 (Trang 15)
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StudentJobApplication.class);
    }
}
