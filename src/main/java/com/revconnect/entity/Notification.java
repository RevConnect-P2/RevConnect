package com.revconnect.entity;

import com.revconnect.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private Long senderId;

    private Long receiverId;

    private Long referenceId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    private boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}