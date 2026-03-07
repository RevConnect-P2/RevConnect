package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "USER_ANALYTICS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "USER_ID")
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;


    // One analytics per user
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private User user;


    @Column(name = "TOTAL_FOLLOWERS")
    private Long totalFollowers = 0L;


    @Column(name = "TOTAL_POSTS")
    private Long totalPosts = 0L;


    @Column(name = "TOTAL_ENGAGEMENT")
    private Long totalEngagement = 0L;


    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;



    // Auto set created timestamp
    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

    }


    // Auto set updated timestamp
    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

    }

}