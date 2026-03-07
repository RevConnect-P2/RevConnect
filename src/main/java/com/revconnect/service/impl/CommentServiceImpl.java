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
import org.springframework.stereotype.Service;

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

    // =====================================
    // GET COMMENTS FOR POST
    // =====================================
    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {

        return commentRepository.findByPost_PostId(postId)
                .stream()
                .map(comment -> CommentResponse.builder()
                        .commentId(comment.getCommentId())
                        .commentText(comment.getCommentText())
                        .username(comment.getUser().getUsername())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();
    }

    // =====================================
    // DELETE COMMENT
    // =====================================
    @Override
    public void deleteComment(Long commentId, String email) {

        Comment comment = commentRepository
                .findByCommentIdAndUser_Email(commentId, email)
                .orElseThrow(() ->
                        new RuntimeException("Comment not found or not authorized"));

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