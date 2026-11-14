package com.studentjob;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DisplayName("Kiểm thử cấu hình Application Context của StudentJobPortal")
class StudentJobApplicationTests {

    @Test
    @DisplayName("Kiểm thử Application Context khởi tạo thành công")
    void contextLoads() {
        StudentJobApplication app = new StudentJobApplication();
        assertNotNull(app);
    }
}
