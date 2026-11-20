package com.uniwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Lớp khởi chạy ứng dụng chính (Spring Boot Application)
 * Kích hoạt Spring Caching để tối ưu hiệu năng và chịu tải
 */
@SpringBootApplication
@EnableCaching
public class UniWorkApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniWorkApplication.class, args);
    }
}
