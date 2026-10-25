package com.capacityconnect.dto.notification;

import com.capacityconnect.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
}