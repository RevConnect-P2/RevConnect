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

    // ✅ ADD THESE TWO
    private Long likeCount;
    private boolean likedByCurrentUser;

    private Long commentCount;

    private Long shareCount;

    private Boolean isSharedPost;
    private String sharedByUsername;
    private String originalAuthorUsername;

}