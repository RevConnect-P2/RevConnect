package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "POST_ANALYTICS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "POST_ID")
        }
)

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


    // One analytics per post
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "POST_ID", nullable = false, unique = true)
    private Post post;


    @Column(name = "TOTAL_LIKES")
    private Long totalLikes = 0L;


    @Column(name = "TOTAL_COMMENTS")
    private Long totalComments = 0L;


    @Column(name = "TOTAL_SHARES")
    private Long totalShares = 0L;


    @Column(name = "REACH_COUNT")
    private Long reachCount = 0L;


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