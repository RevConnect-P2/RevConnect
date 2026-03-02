package com.revconnect.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {

    private Long postId;

    private String content;

    private String postType;

    private Boolean pinned;

    private String ctaText;
    private String ctaLink;

    private LocalDateTime scheduledAt;

    private LocalDateTime createdAt;

    // Minimal user info (NO password, NO entity)
    private Long userId;
    private String username;

    // Hashtags as plain strings
    private List<String> hashtags;
}