package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "POSTS")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "POST_ID")
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Lob
    @Column(name = "CONTENT")
    private String content;

    @Column(name = "POST_TYPE")
    private String postType;

    @Column(name = "PINNED")
    private Boolean pinned;

    @Column(name = "CTA_TEXT")
    private String ctaText;

    @Column(name = "CTA_LINK")
    private String ctaLink;

    @Column(name = "SCHEDULED_AT")
    private LocalDateTime scheduledAt;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

}