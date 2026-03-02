package com.revconnect.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequest {

    // Post text content
    private String content;

    // NORMAL / PROMOTIONAL (string for now)
    private String postType;

    // Pin post on profile
    private Boolean pinned;

    // Call-to-action (only for promotional posts)
    private String ctaText;
    private String ctaLink;

    // For scheduled posts
    private LocalDateTime scheduledAt;

    // List of hashtags without #
    // Example: ["java", "springboot"]
    private List<String> hashtags;
}