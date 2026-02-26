package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "FOLLOWS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"FOLLOWER_ID", "FOLLOWING_ID"})
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "FOLLOW_ID")
    private Long followId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLLOWER_ID", nullable = false)
    private User follower;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLLOWING_ID", nullable = false)
    private User following;


    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;



    // ✅ ONLY ONE PrePersist
    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();
        validateFollow();

    }


    // ✅ ONLY ONE PreUpdate
    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
        validateFollow();

    }


    // validation method (NO annotation here)
    private void validateFollow() {

        if (follower != null &&
                following != null &&
                follower.getUserId().equals(following.getUserId())) {

            throw new IllegalStateException("User cannot follow themselves");

        }

    }

}