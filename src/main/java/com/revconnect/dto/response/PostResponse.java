package com.revconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long postId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String postType;
    private String ctaLink;
    private String ctaText;
    private Boolean pinned;
    private LocalDateTime scheduledAt;
    private Long userId;
}