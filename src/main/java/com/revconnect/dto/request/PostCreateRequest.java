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

    // NORMAL / PROMOTIONAL
    private String postType;

    // Pin post on profile
    private Boolean pinned;

    // Call-to-action (only for promotional posts)
    private String ctaText;
    private String ctaLink;

    // For scheduled posts
    private LocalDateTime scheduledAt;

    // Hashtags (NOT YOUR MODULE – untouched)
    private List<String> hashtags;

    // 🆕 Product / Service Tags
    private List<TagRequest> tags;

}