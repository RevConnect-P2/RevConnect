package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "POST_ANALYTICS")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PostAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;


    @OneToOne
    @JoinColumn(name = "POST_ID")
    private Post post;


    @Column(name = "TOTAL_LIKES")
    private Long totalLikes;


    @Column(name = "TOTAL_COMMENTS")
    private Long totalComments;


    @Column(name = "TOTAL_SHARES")
    private Long totalShares;


    @Column(name = "REACH_COUNT")
    private Long reachCount;


    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

}