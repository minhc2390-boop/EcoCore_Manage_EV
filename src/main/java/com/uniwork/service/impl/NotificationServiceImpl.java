package com.uniwork.service.impl;

import com.uniwork.dto.NotificationDTO;
import com.uniwork.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Cài đặt hệ thống thông báo thời gian thực dùng Server-Sent Events (SSE)
 * Tối ưu hóa cho quy mô vừa: quản lý bộ kết nối trên RAM, tự dọn dẹp kết nối chết
 * @author Cao Trần Duy Minh - PC11166 (Trưởng nhóm / Lead Backend)
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    // Danh sách các kết nối SSE đang mở theo từng username (hỗ trợ 1 user mở nhiều tab)
    private final Map<String, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    // Danh sách các kết nối vãng lai / khách chưa đăng nhập
    private final List<SseEmitter> anonymousEmitters = new CopyOnWriteArrayList<>();

    // Lưu trữ tạm thời 20 thông báo gần nhất cho từng người dùng
    private final Map<String, List<NotificationDTO>> userNotificationHistory = new ConcurrentHashMap<>();

    // Thời gian timeout cho 1 kết nối SSE: 30 phút (sau đó client tự reconnect)
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;

    @Override
    public SseEmitter subscribe(String username) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        if (username != null && !username.isBlank()) {
            String key = username.trim();
            userEmitters.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(emitter);

            emitter.onCompletion(() -> removeEmitter(key, emitter));
            emitter.onTimeout(() -> removeEmitter(key, emitter));
            emitter.onError(e -> removeEmitter(key, emitter));

            log.debug("Người dùng '{}' đã kết nối SSE Realtime thành công", key);
        } else {
            anonymousEmitters.add(emitter);
            emitter.onCompletion(() -> anonymousEmitters.remove(emitter));
            emitter.onTimeout(() -> anonymousEmitters.remove(emitter));
            emitter.onError(e -> anonymousEmitters.remove(emitter));
        }

        // Gửi ngay gói tin chào mừng kèm thông tin kết nối ban đầu
        try {
            emitter.send(SseEmitter.event()
                    .name("CONNECTED")
                    .data(Map.of(
                            "status", "CONNECTED",
                            "message", "Kết nối hệ thống thời gian thực UniWork thành công!",
                            "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"))
                    )));
        } catch (IOException e) {
            log.warn("Lỗi gửi gói tin chào mừng SSE: {}", e.getMessage());
        }

        return emitter;
    }

    @Override
    public void sendToUser(String username, NotificationDTO notification) {
        if (username == null || username.isBlank() || notification == null) {
            return;
        }
        String key = username.trim();

        // Lưu vào lịch sử thông báo của người dùng
        saveToHistory(key, notification);

        List<SseEmitter> emitters = userEmitters.get(key);
        if (emitters == null || emitters.isEmpty()) {
            log.info("Người dùng '{}' hiện không online, thông báo ID {} đã được lưu vào lịch sử", key, notification.getId());
            return;
        }

        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
        log.info("Đã gửi thông báo Realtime tới '{}': {}", key, notification.getTitle());
    }

    @Override
    public void broadcast(NotificationDTO notification) {
        if (notification == null) {
            return;
        }

        // Lưu vào lịch sử của tất cả user đang online
        for (String user : userEmitters.keySet()) {
            saveToHistory(user, notification);
        }

        // Gửi tới tất cả user đăng nhập
        for (Map.Entry<String, List<SseEmitter>> entry : userEmitters.entrySet()) {
            List<SseEmitter> emitters = entry.getValue();
            List<SseEmitter> dead = new ArrayList<>();
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().name("notification").data(notification));
                } catch (Exception e) {
                    dead.add(emitter);
                }
            }
            emitters.removeAll(dead);
        }

        // Gửi tới cả khách vãng lai
        List<SseEmitter> deadAnon = new ArrayList<>();
        for (SseEmitter emitter : anonymousEmitters) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(notification));
            } catch (Exception e) {
                deadAnon.add(emitter);
            }
        }
        anonymousEmitters.removeAll(deadAnon);
        log.info("Đã phát sóng thông báo toàn hệ thống (Broadcast): {}", notification.getTitle());
    }

    @Override
    public List<NotificationDTO> getRecentNotifications(String username) {
        if (username == null || username.isBlank()) {
            return Collections.emptyList();
        }
        List<NotificationDTO> list = userNotificationHistory.get(username.trim());
        return list != null ? new ArrayList<>(list) : Collections.emptyList();
    }

    @Override
    public void markAllAsRead(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        List<NotificationDTO> list = userNotificationHistory.get(username.trim());
        if (list != null) {
            for (NotificationDTO n : list) {
                n.setUnread(false);
            }
        }
    }

    private void removeEmitter(String username, SseEmitter emitter) {
        List<SseEmitter> emitters = userEmitters.get(username);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                userEmitters.remove(username);
            }
        }
    }

    private void saveToHistory(String username, NotificationDTO notification) {
        userNotificationHistory.compute(username, (k, list) -> {
            if (list == null) {
                list = new CopyOnWriteArrayList<>();
            }
            list.add(0, notification);
            while (list.size() > 20) {
                list.remove(list.size() - 1);
            }
            return list;
        });
    }
}
