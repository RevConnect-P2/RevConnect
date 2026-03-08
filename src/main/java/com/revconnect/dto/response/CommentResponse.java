package com.revconnect.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long commentId;
    private String commentText;

    private LocalDateTime createdAt;

    private String username;

    // ADD THESE
    private Long userId;        // comment owner
    private Long postOwnerId;   // post owner
}