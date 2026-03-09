package com.revconnect.service.impl;

import com.revconnect.entity.Post;
import com.revconnect.entity.Share;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.ShareRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.NotificationService;
import com.revconnect.service.ShareService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final ShareRepository shareRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // =========================
    // SHARE POST
    // =========================
    @Override
    public void sharePost(Long postId, String email) {

        User user = getUserByEmail(email);
        Post post = getPostById(postId);

        Optional<Share> existingShare =
                shareRepository.findByOriginalPost_PostIdAndSharedBy_UserId(
                        postId,
                        user.getUserId()
                );

        // If already shared → throw error
        if (existingShare.isPresent()) {
            throw new RuntimeException("Already shared");
        }

        Share share = Share.builder()
                .originalPost(post)
                .sharedBy(user)
                .build();

        shareRepository.save(share);

        createShareNotification(user, post);
    }

    // =========================
    // UNSHARE POST
    // =========================
    @Override
    public void unsharePost(Long postId, String email) {

        User user = getUserByEmail(email);

        Share share = shareRepository
                .findByOriginalPost_PostIdAndSharedBy_UserId(
                        postId,
                        user.getUserId()
                )
                .orElseThrow(() -> new RuntimeException("Share not found"));

        shareRepository.delete(share);
    }

    // =========================
    // TOGGLE SHARE
    // =========================
    @Override
    public boolean toggleShare(Long postId, String email) {

        User user = getUserByEmail(email);
        Post post = getPostById(postId);

        Optional<Share> existingShare =
                shareRepository.findByOriginalPost_PostIdAndSharedBy_UserId(
                        postId,
                        user.getUserId()
                );

        // already shared → unshare
        if (existingShare.isPresent()) {

            shareRepository.delete(existingShare.get());
            return false;
        }

        // create share
        Share share = Share.builder()
                .originalPost(post)
                .sharedBy(user)
                .build();

        shareRepository.save(share);

        createShareNotification(user, post);

        return true;
    }

    // =========================
    // SHARE COUNT
    // =========================
    @Override
    public Long getShareCount(Long postId) {

        return shareRepository.countByOriginalPost_PostId(postId);
    }

    // =========================
    // USERS WHO SHARED
    // =========================
    @Override
    public List<String> getUsersWhoShared(Long postId) {

        return shareRepository.findUsernamesWhoShared(postId);
    }

    // =========================
    // HELPER METHODS
    // =========================

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Post getPostById(Long postId) {

        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    // =========================
    // CREATE SHARE NOTIFICATION
    // =========================
    private void createShareNotification(User sender, Post post) {

        Long senderId = sender.getUserId();
        Long receiverId = post.getUser().getUserId();

        if (!senderId.equals(receiverId)) {

            notificationService.createNotification(
                    senderId,
                    receiverId,
                    post.getPostId(),
                    NotificationType.SHARE,
                    null
            );
        }
    }
}