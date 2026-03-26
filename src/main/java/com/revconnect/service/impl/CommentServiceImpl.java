package com.revconnect.service.impl;

import com.revconnect.dto.response.CommentResponse;
import com.revconnect.entity.Comment;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.CommentRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.AnalyticsService;
import com.revconnect.service.CommentService;
import com.revconnect.service.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final Logger logger =
            LogManager.getLogger(CommentServiceImpl.class);

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AnalyticsService analyticsService;

    // ADD COMMENT
    @Override
    public void addComment(Long postId, String email, String commentText) {

        logger.info("User {} adding comment to post {}", email, postId);

        User user = getUserByEmail(email);
        Post post = getPostById(postId);

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .commentText(commentText)
                .build();

        commentRepository.save(comment);

        analyticsService.incrementComments(post);
        logger.info("Comment added successfully by {} on post {}", email, postId);

        createCommentNotification(user, post, commentText);
    }

    // GET COMMENTS FOR POST
    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {

        logger.info("Fetching comments for post {}", postId);

        return commentRepository.findByPost_PostIdOrderByCreatedAtDesc(postId)
                .stream()
                .map(comment -> CommentResponse.builder()
                        .commentId(comment.getCommentId())
                        .commentText(comment.getCommentText())
                        .username(comment.getUser().getUsername())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .toList();
    }

    // DELETE COMMENT
    @Override
    public void deleteComment(Long commentId, String email) {

        logger.info("User {} attempting to delete comment {}", email, commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.error("Comment {} not found", commentId);
                    return new RuntimeException("Comment not found");
                });

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User {} not found", email);
                    return new RuntimeException("User not found");
                });

        boolean isCommentOwner =
                comment.getUser().getUserId().equals(user.getUserId());

        boolean isPostOwner =
                comment.getPost().getUser().getUserId().equals(user.getUserId());

        if(!isCommentOwner && !isPostOwner){

            logger.warn("User {} attempted unauthorized deletion of comment {}", email, commentId);

            throw new RuntimeException("You cannot delete this comment");
        }

        commentRepository.delete(comment);
        analyticsService.decrementComments(comment.getPost());

        logger.info("Comment {} deleted by user {}", commentId, email);
    }

    // HELPER METHODS

    private User getUserByEmail(String email) {

        logger.debug("Fetching user by email {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email {}", email);
                    return new RuntimeException("User not found");
                });
    }

    private Post getPostById(Long postId) {

        logger.debug("Fetching post {}", postId);

        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post {} not found", postId);
                    return new RuntimeException("Post not found");
                });
    }

    private void createCommentNotification(User sender, Post post, String commentText) {

        Long senderId = sender.getUserId();
        Long receiverId = post.getUser().getUserId();

        // prevent self notification
        if (!senderId.equals(receiverId)) {

            logger.debug("Sending comment notification from {} to {}", senderId, receiverId);

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