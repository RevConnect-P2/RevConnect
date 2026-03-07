package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "HASHTAGS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "TAG_NAME")
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "HASHTAG_ID")
    private Long hashtagId;


    // prevent duplicate hashtags like #java twice
    @Column(name = "TAG_NAME", nullable = false, unique = true, length = 100)
    private String tagName;


    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;



    // Oracle timestamp auto insert
    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

    }

}