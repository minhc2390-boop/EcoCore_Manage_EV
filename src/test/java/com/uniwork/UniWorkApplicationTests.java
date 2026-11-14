package com.uniwork;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DisplayName("Kiểm thử cấu hình Application Context của StudentJobPortal")
class UniWorkApplicationTests {

    @Test
    @DisplayName("Kiểm thử Application Context khởi tạo thành công")
    void contextLoads() {
        UniWorkApplication app = new UniWorkApplication();
        assertNotNull(app);
    }
}
