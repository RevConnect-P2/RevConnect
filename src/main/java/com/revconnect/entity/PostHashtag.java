package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "POST_HASHTAGS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"POST_ID", "HASHTAG_ID"})
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PostHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;


    // Many mappings belong to one post
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "POST_ID", nullable = false)
    private Post post;


    // Many mappings belong to one hashtag
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "HASHTAG_ID", nullable = false)
    private Hashtag hashtag;


    @Column(name = "TAGGED_AT", updatable = false)
    private LocalDateTime taggedAt;



    // Auto timestamp when inserted
    @PrePersist
    protected void onCreate() {

        taggedAt = LocalDateTime.now();

    }

}