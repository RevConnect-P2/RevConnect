package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "USERS")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // ✅ Best for Oracle
    @Column(name = "USER_ID")
    private Long userId;


    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;


    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;


    @Column(name = "PASSWORD", nullable = false)
    private String password;


    @Column(name = "USER_TYPE")
    private String userType;


    // Oracle stores Boolean as NUMBER(1)
    @Column(name = "IS_PRIVATE")
    private Boolean isPrivate;


    @Column(name = "SECURITY_QUESTION")
    private String securityQuestion;


    @Column(name = "SECURITY_ANSWER")
    private String securityAnswer;


    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    // ✅ ADD THIS PART
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserProfile userProfile;


    // Automatically insert timestamp
    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

    }


    // Automatically update timestamp
    @PreUpdate
    public void preUpdate() {

        updatedAt = LocalDateTime.now();

    }

    @Column(name = "notifications_enabled")
    private Boolean notificationsEnabled = true;
}