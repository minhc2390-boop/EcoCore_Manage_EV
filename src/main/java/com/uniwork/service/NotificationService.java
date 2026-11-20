package com.uniwork.service;

import com.uniwork.dto.NotificationDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * Service quản lý gửi và phát thông báo thời gian thực qua Server-Sent Events (SSE)
 * @author Cao Trần Duy Minh - PC11166 (Trưởng nhóm / Lead Backend)
 */
public interface NotificationService {

    SseEmitter subscribe(String username);

    void sendToUser(String username, NotificationDTO notification);

    void broadcast(NotificationDTO notification);

    List<NotificationDTO> getRecentNotifications(String username);

    void markAllAsRead(String username);
}
