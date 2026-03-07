package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATIONS")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "NOTIFICATION_ID")
    private Long notificationId;


    // User receiving notification
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;


    // LIKE, COMMENT, FOLLOW, CONNECTION, SHARE
    @Column(name = "TYPE", nullable = false, length = 50)
    private String type;


    // ID of related entity (POST_ID, COMMENT_ID etc)
    @Column(name = "REFERENCE_ID")
    private Long referenceId;


    @Column(name = "MESSAGE", length = 500)
    private String message;


    // Oracle stores Boolean as NUMBER(1)
    @Column(name = "IS_READ")
    private Boolean isRead = false;


    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;



    // Auto set created timestamp
    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

        if (isRead == null) {
            isRead = false;
        }

    }


    // Auto set updated timestamp
    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

    }

}