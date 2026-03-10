package com.revconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequest {

    // =========================
    // POST CONTENT
    // =========================
    @NotBlank(message = "Post content cannot be empty")
    @Size(max = 5000, message = "Post content is too long")
    private String content;


    // =========================
    // POST TYPE
    // NORMAL / PROMOTIONAL
    // =========================
    @NotBlank(message = "Post type is required")
    private String postType;


    // =========================
    // PIN POST ON PROFILE
    // =========================
    private Boolean pinned;


    // =========================
    // CTA (ONLY FOR PROMOTIONAL)
    // =========================
    private String ctaText;

    private String ctaLink;


    // =========================
    // SCHEDULED POST
    // =========================
    private LocalDateTime scheduledAt;


    // =========================
    // HASHTAGS
    // =========================
    private List<String> hashtags;


    // =========================
    // PRODUCT / SERVICE TAGS
    // =========================
    private List<TagRequest> tags;
}