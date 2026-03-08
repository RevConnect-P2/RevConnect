package com.revconnect.service.impl;

import com.revconnect.dto.response.CommentResponse;
import com.revconnect.entity.Comment;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.CommentRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public void addComment(Long postId, String email, String commentText) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .commentText(commentText)
                .build();

        commentRepository.save(comment);
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {

        return commentRepository.findByPost_PostId(postId)
                .stream()
                .map(comment -> CommentResponse.builder()
                        .commentId(comment.getCommentId())
                        .commentText(comment.getCommentText())
                        .username(comment.getUser().getUsername())
                        .createdAt(comment.getCreatedAt())
                        .userId(comment.getUser().getUserId())                // NEW
                        .postOwnerId(comment.getPost().getUser().getUserId()) // NEW
                        .build())
                .toList();
    }

    @Override
    public void deleteComment(Long commentId, String email) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // person who wrote the comment
        String commentOwnerEmail = comment.getUser().getEmail();

        // owner of the post
        String postOwnerEmail = comment.getPost().getUser().getEmail();

        // check authorization
        if (!email.equals(commentOwnerEmail) && !email.equals(postOwnerEmail)) {
            throw new RuntimeException("You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }
}