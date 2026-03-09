package com.revconnect.entity;

import com.revconnect.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notification_receiver", columnList = "receiver_id"),
                @Index(name = "idx_notification_sender", columnList = "sender_id")
        }
)
@Getter
@Setter
public class Notification {

    // ========================
    // PRIMARY KEY (ORACLE SEQUENCE)
    // ========================
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq_generator")
    @SequenceGenerator(
            name = "notification_seq_generator",
            sequenceName = "notifications_seq",
            allocationSize = 1
    )
    @Column(name = "notification_id")
    private Long id;

    // ========================
    // Sender (who triggered action)
    // ========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    // ========================
    // Receiver (who gets notification)
    // ========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    // ========================
    // Reference post / entity
    // ========================
    @Column(name = "reference_id")
    private Long referenceId;

    // ========================
    // Notification type
    // ========================
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    // ========================
    // Notification message
    // ========================
    @Column(name = "message", length = 500)
    private String message;

    // ========================
    // Read status
    // ========================
    @Column(name = "is_read")
    private boolean read = false;

    // ========================
    // Created timestamp
    // ========================
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ========================
    // Updated timestamp
    // ========================
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========================
    // Auto timestamps
    // ========================
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}