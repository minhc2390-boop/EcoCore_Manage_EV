package com.uniwork.config;

import com.uniwork.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir.toString() + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Đăng ký bộ lọc AuthInterceptor để bảo vệ các URL nhạy cảm
        registry.addInterceptor(authInterceptor)
                // 1. Những đường dẫn BẮT BUỘC đăng nhập mới được vào
                .addPathPatterns(
                        "/profile/**",
                        "/employer/**",
                        "/admin/**")
                // 2. Những đường dẫn CÔNG KHAI (ai cũng vào được)
                .excludePathPatterns(
                        "/",
                        "/home",
                        "/jobs/**",
                        "/login",
                        "/register",
                        "/logout",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/uploads/**");
    }
}