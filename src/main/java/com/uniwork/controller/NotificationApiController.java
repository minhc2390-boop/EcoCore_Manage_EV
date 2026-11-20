package com.uniwork.controller;

import com.uniwork.dto.ApiResponse;
import com.uniwork.dto.NotificationDTO;
import com.uniwork.entity.User;
import com.uniwork.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * REST API phục vụ kết nối luồng thông báo thời gian thực (SSE Stream)
 * @author Cao Trần Duy Minh - PC11166 (Trưởng nhóm / Lead Backend)
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationApiController {

    private final NotificationService notificationService;

    /**
     * Mở luồng kết nối Server-Sent Events (SSE) thời gian thực
     * Trình duyệt kết nối qua EventSource('/api/notifications/stream')
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        String username = currentUser != null ? currentUser.getUsername() : null;
        return notificationService.subscribe(username);
    }

    /**
     * Lấy danh sách các thông báo gần nhất của người dùng
     */
    @GetMapping("/recent")
    public ResponseEntity<List<NotificationDTO>> getRecent(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(notificationService.getRecentNotifications(currentUser.getUsername()));
    }

    /**
     * Đánh dấu toàn bộ thông báo đã đọc
     */
    @PostMapping("/mark-read")
    public ResponseEntity<ApiResponse<String>> markRead(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            notificationService.markAllAsRead(currentUser.getUsername());
        }
        return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu đã đọc!"));
    }

    /**
     * API gửi thông báo thử nghiệm (phục vụ kiểm tra trực tiếp và demo)
     */
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<String>> testNotification(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        String username = currentUser != null ? currentUser.getUsername() : "khach_demo";

        NotificationDTO testNotif = NotificationDTO.builder()
                .id(UUID.randomUUID().toString())
                .title("Thông báo thời gian thực UniWork!")
                .message("Hệ thống thông báo Realtime qua Server-Sent Events (SSE) đang hoạt động ổn định và tức thì.")
                .type("primary")
                .targetUrl("/jobs")
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")))
                .unread(true)
                .build();

        if (currentUser != null) {
            notificationService.sendToUser(username, testNotif);
        } else {
            notificationService.broadcast(testNotif);
        }

        return ResponseEntity.ok(ApiResponse.success("Đã kích hoạt gửi thông báo Realtime mẫu!"));
    }
}
