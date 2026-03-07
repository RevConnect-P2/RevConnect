package com.revconnect.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long commentId;
    private String commentText;
//    private String userEmail;
    private LocalDateTime createdAt;
    private String username;
}