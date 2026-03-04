package com.revconnect.service;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;

public interface CommentService {
    public void addComment(Post post, User currentUser);
}
import com.revconnect.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

    void addComment(Long postId, String email, String commentText);

    List<CommentResponse> getCommentsByPostId(Long postId);

    void deleteComment(Long commentId, String email);
}
