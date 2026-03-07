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

        // 🔔 Notification
        if (!post.getUser().getUserId().equals(user.getUserId())) {

            notificationService.createNotification(
                    user.getUserId(),
                    post.getUser().getUserId(),
                    postId,
                    NotificationType.COMMENT,
                    commentText
            );
        }
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
                        .build())
                .toList();
    }

    @Override
    public void deleteComment(Long commentId, String email) {

        Comment comment = commentRepository
                .findByCommentIdAndUser_Email(commentId, email)
                .orElseThrow(() -> new RuntimeException("Comment not found or not authorized"));

        commentRepository.delete(comment);
    }
}