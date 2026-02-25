package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "FOLLOWS")

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

    @ManyToOne
    @JoinColumn(name = "FOLLOWER_ID")
    private User follower;

    @ManyToOne
    @JoinColumn(name = "FOLLOWING_ID")
    private User following;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

}