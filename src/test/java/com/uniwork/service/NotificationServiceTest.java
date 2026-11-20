package com.uniwork.service;

import com.uniwork.dto.NotificationDTO;
import com.uniwork.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho NotificationService (Server-Sent Events)
 * @author Cao Trần Duy Minh - PC11166 (Trưởng nhóm / Lead Backend)
 */
@DisplayName("Unit Test cho NotificationService (SSE Realtime)")
class NotificationServiceTest {

    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl();
    }

    @Test
    @DisplayName("Kiểm tra đăng ký kênh SSE thành công cho người dùng")
    void should_CreateSseEmitter_When_UserSubscribes() {
        SseEmitter emitter = notificationService.subscribe("sinhvien_cantho");
        assertNotNull(emitter);
    }

    @Test
    @DisplayName("Kiểm tra gửi thông báo cho người dùng và lưu vào lịch sử")
    void should_SaveAndSendNotification_When_SentToUser() {
        NotificationDTO notif = NotificationDTO.builder()
                .id("test-1")
                .title("Đơn ứng tuyển mới")
                .message("Sinh viên vừa nộp đơn")
                .type("success")
                .unread(true)
                .build();

        notificationService.subscribe("highlandscantho");
        notificationService.sendToUser("highlandscantho", notif);

        List<NotificationDTO> recent = notificationService.getRecentNotifications("highlandscantho");
        assertFalse(recent.isEmpty());
        assertEquals("test-1", recent.get(0).getId());
        assertEquals("Đơn ứng tuyển mới", recent.get(0).getTitle());
        assertTrue(recent.get(0).isUnread());
    }

    @Test
    @DisplayName("Kiểm tra đánh dấu tất cả thông báo đã đọc")
    void should_MarkAllAsRead_Success() {
        NotificationDTO notif = NotificationDTO.builder()
                .id("test-read")
                .title("Tiêu đề")
                .message("Nội dung")
                .unread(true)
                .build();

        notificationService.sendToUser("sinhvien_cantho", notif);
        notificationService.markAllAsRead("sinhvien_cantho");

        List<NotificationDTO> list = notificationService.getRecentNotifications("sinhvien_cantho");
        assertFalse(list.isEmpty());
        assertFalse(list.get(0).isUnread());
    }

    @Test
    @DisplayName("Kiểm tra phát sóng thông báo toàn hệ thống (Broadcast)")
    void should_BroadcastNotification_Success() {
        NotificationDTO broadcastNotif = NotificationDTO.builder()
                .id("broadcast-1")
                .title("Việc làm mới")
                .message("Highlands Coffee vừa đăng tin mới")
                .type("primary")
                .unread(true)
                .build();

        notificationService.subscribe("user_a");
        notificationService.broadcast(broadcastNotif);

        List<NotificationDTO> list = notificationService.getRecentNotifications("user_a");
        assertFalse(list.isEmpty());
        assertEquals("broadcast-1", list.get(0).getId());
    }
}
