package com.uniwork.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO đại diện cho một thông báo thời gian thực (Real-time Notification)
 * @author Cao Trần Duy Minh - PC11166 (Trưởng nhóm / Lead Backend)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO implements Serializable {
    private String id;
    private String title;
    private String message;
    private String type; // info, success, warning, primary
    private String targetUrl;
    private String createdAt;
    private boolean unread;
}
