package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "POST_LIKES",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"POST_ID", "USER_ID"})
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "LIKE_ID")
    private Long likeId;


    // Many likes belong to one post
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "POST_ID", nullable = false)
    private Post post;


    // Many likes belong to one user
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;


    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;



    // Auto timestamp on insert
    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

    }


    // Auto timestamp on update
    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

    }

}