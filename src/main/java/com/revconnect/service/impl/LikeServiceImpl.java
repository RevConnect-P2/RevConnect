package com.revconnect.service.impl;

import com.revconnect.entity.Post;
import com.revconnect.entity.PostLike;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.PostLikeRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.LikeService;
import com.revconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final NotificationService notificationService;

    @Override
    public void likePost(Long postId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<PostLike> existingLike =
                postLikeRepository.findByPost_PostIdAndUser_Email(postId, email);

        if (existingLike.isPresent()) {
            throw new RuntimeException("You already liked this post");
        }

        PostLike like = PostLike.builder()
                .post(post)
                .user(user)
                .build();

        postLikeRepository.save(like);

        // 🔔 Notification
        if (!post.getUser().getUserId().equals(user.getUserId())) {

            notificationService.createNotification(
                    user.getUserId(),
                    post.getUser().getUserId(),
                    postId,
                    NotificationType.LIKE,
                    null
            );
        }
    }

    @Override
    public void unlikePost(Long postId, String email) {

        PostLike like = postLikeRepository
                .findByPost_PostIdAndUser_Email(postId, email)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        postLikeRepository.delete(like);
    }

    @Override
    public boolean toggleLike(Long postId, String principalValue) {

        User user = userRepository.findByEmail(principalValue)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<PostLike> existingLike =
                postLikeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {

            postLikeRepository.delete(existingLike.get());
            return false;

        } else {

            PostLike like = new PostLike();
            like.setUser(user);
            like.setPost(post);

            postLikeRepository.save(like);

            // 🔔 Notification
            if (!post.getUser().getUserId().equals(user.getUserId())) {

                notificationService.createNotification(
                        user.getUserId(),
                        post.getUser().getUserId(),
                        postId,
                        NotificationType.LIKE,
                        null
                );
            }

            return true;
        }
    }

    @Override
    public List<String> getUsersWhoLiked(Long postId) {

        return postLikeRepository.findByPost_PostId(postId)
                .stream()
                .map(like -> like.getUser().getUsername())
                .toList();
    }

}