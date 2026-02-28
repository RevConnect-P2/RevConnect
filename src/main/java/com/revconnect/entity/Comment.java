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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
    @SequenceGenerator(
            name = "comment_seq",
            sequenceName = "comment_seq",
            allocationSize = 1
    )
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


    // FIX FOR ORACLE CLOB
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "COMMENT_TEXT", nullable = false, columnDefinition = "CLOB")
    private String commentText;


    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;



    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        validateComment();

    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

        validateComment();

    }



    private void validateComment() {

        if (commentText == null || commentText.trim().isEmpty()) {

            throw new IllegalStateException("Comment cannot be empty");

        }

    }

}