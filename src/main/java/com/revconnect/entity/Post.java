package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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



    // =========================
    // RELATIONSHIPS (CASCADE)
    // =========================


    // Comments
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;


    // Likes
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes;


    // Shares
    @OneToMany(mappedBy = "originalPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Share> shares;


    // Hashtags
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHashtag> hashtags;


    // Tags
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> tags;


    // Saved posts
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedPost> savedPosts;



    // =========================
    // TIMESTAMPS
    // =========================


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}