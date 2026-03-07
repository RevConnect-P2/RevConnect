package com.revconnect.service;

import com.revconnect.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

    void addComment(Long postId, String email, String commentText);

    List<CommentResponse> getCommentsByPostId(Long postId);

    void deleteComment(Long commentId, String email);

}