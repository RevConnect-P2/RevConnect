package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "POST_COMMENTS")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "COMMENT_ID")
    private Long commentId;


    // Many comments belong to one post
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "POST_ID", nullable = false)
    private Post post;


    // Many comments belong to one user
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;


    // Oracle supports large text using CLOB
    @Lob
    @Column(name = "COMMENT_TEXT", nullable = false)
    private String commentText;


    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;



    // Auto set created timestamp
    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

        validateComment();
    }


    // Auto set updated timestamp
    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

        validateComment();
    }


    // Prevent empty comment
    private void validateComment() {

        if (commentText == null || commentText.trim().isEmpty()) {

            throw new IllegalStateException("Comment cannot be empty");

        }

    }

}