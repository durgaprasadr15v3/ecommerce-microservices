package com.demo.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String userId;
    private String eventType;
    private String subject;
    private String body;
    private String channel;    // EMAIL, SMS, PUSH
    private LocalDateTime timestamp;
}
