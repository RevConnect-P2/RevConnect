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


    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;


    @Column(name = "TYPE")
    private String type;


    @Column(name = "REFERENCE_ID")
    private Long referenceId;


    @Column(name = "MESSAGE")
    private String message;


    @Column(name = "IS_READ")
    private Boolean isRead;


    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

}