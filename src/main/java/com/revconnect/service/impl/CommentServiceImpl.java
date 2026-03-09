package com.revconnect.service.impl;

import com.revconnect.dto.response.CommentResponse;
import com.revconnect.entity.Comment;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.CommentRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.CommentService;
import com.revconnect.service.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // =====================================
    // ADD COMMENT
    // =====================================
    @Override
    public void addComment(Long postId, String email, String commentText) {

        User user = getUserByEmail(email);
        Post post = getPostById(postId);

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .commentText(commentText)
                .build();

        commentRepository.save(comment);

        createCommentNotification(user, post, commentText);
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {

        return commentRepository.findByPost_PostId(postId)
                .stream()
                .map(comment -> CommentResponse.builder()
                        .commentId(comment.getCommentId())
                        .commentText(comment.getCommentText())
                        .username(comment.getUser().getUsername()) // ✅ FIX
                        .createdAt(comment.getCreatedAt())
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this comment");
        }

        commentRepository.delete(comment);
    }

    // =====================================
    // HELPER METHODS
    // =====================================

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Post getPostById(Long postId) {

        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    private void createCommentNotification(User sender, Post post, String commentText) {

        Long senderId = sender.getUserId();
        Long receiverId = post.getUser().getUserId();

        // prevent self notification
        if (!senderId.equals(receiverId)) {

            notificationService.createNotification(
                    senderId,
                    receiverId,
                    post.getPostId(),
                    NotificationType.COMMENT,
                    commentText
            );
        }
    }
}