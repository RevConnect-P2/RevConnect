package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "HASHTAGS")

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

    @Column(name = "TAG_NAME")
    private String tagName;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

}