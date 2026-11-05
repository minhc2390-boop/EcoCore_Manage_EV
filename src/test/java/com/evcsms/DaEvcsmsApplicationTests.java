package com.evcsms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Kiểm thử cấu hình chính của ứng dụng")
class DaEvcsmsApplicationTests {

    @Test
    @DisplayName("Kiểm thử khởi tạo context")
    void contextLoads() {
        DaEvcsmsApplication app = new DaEvcsmsApplication();
        assertNotNull(app);
    }
}

