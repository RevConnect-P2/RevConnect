package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_ANALYTICS")

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


    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;


    @Column(name = "TOTAL_FOLLOWERS")
    private Long totalFollowers;


    @Column(name = "TOTAL_POSTS")
    private Long totalPosts;


    @Column(name = "TOTAL_ENGAGEMENT")
    private Long totalEngagement;


    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

}