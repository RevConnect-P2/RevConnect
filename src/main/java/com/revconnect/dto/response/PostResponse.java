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

    // =========================
    // POST DETAILS
    // =========================
    private Long postId;

    private String content;

    private String postType;

    private Boolean pinned;

    private String ctaText;

    private String ctaLink;

    private LocalDateTime scheduledAt;

    private LocalDateTime createdAt;



    // =========================
    // AUTHOR
    // =========================
    private Long userId;

    private String username;



    // =========================
    // HASHTAGS
    // =========================
    private List<String> hashtags;



    // =========================
    // PRODUCT / SERVICE TAGS
    // =========================
    private List<TagResponse> tags;



    // =========================
    // INTERACTIONS
    // =========================
    @Builder.Default
    private Long likeCount = 0L;

    @Builder.Default
    private boolean likedByCurrentUser = false;

    @Builder.Default
    private Long commentCount = 0L;

    @Builder.Default
    private Long shareCount = 0L;


    // =========================
// SHARE INFO (FOR FEED)
// =========================
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonProperty("isSharedPost")
    private boolean isSharedPost = false;

    private String sharedByUsername;

    private String originalAuthorUsername;
    private List<CommentResponse> comments;
}