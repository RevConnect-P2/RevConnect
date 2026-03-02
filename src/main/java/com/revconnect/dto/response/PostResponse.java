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

    private Long userId;
    private String username;

    private List<String> hashtags;

    // 🆕 Tagged Products / Services
    private List<TagResponse> tags;
}