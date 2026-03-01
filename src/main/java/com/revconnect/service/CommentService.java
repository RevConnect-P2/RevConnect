package com.revconnect.service;

import com.revconnect.dto.response.CommentResponse;
import com.revconnect.entity.Comment;

import java.util.List;

public interface CommentService {

    void addComment(Long postId, String email, String commentText);

//    List<Comment> getCommentsByPost(Long postId);
    List<CommentResponse> getCommentsByPost(Long postId);
    void deleteComment(Long commentId, String email);
}