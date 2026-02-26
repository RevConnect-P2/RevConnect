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


    // Many posts belong to one user
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;


    // Oracle CLOB mapping
    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;


    @Column(name = "POST_TYPE")
    private String postType;


    // Oracle stores Boolean as NUMBER(1)
    @Column(name = "PINNED")
    private Boolean pinned = false;


    @Column(name = "CTA_TEXT")
    private String ctaText;


    @Column(name = "CTA_LINK")
    private String ctaLink;


    @Column(name = "SCHEDULED_AT")
    private LocalDateTime scheduledAt;


    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;



    // Auto set created time
    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

    }


    // Auto update time
    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

    }

}