package com.revconnect.service.impl;

import com.revconnect.entity.Post;
import com.revconnect.entity.Share;
import com.revconnect.entity.User;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.ShareRepository;
import com.revconnect.repository.UserRepository;
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

    // =========================
    // SHARE POST
    // =========================
    @Override
    public void sharePost(Long postId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<Share> existingShare =
                shareRepository.findByOriginalPost_PostIdAndSharedBy_UserId(
                        postId,
                        user.getUserId()
                );

        // already shared → do nothing
        if (existingShare.isPresent()) {
            return;
        }

        Share share = Share.builder()
                .originalPost(post)
                .sharedBy(user)
                .build();

        shareRepository.save(share);
    }

    // =========================
    // UNSHARE POST
    // =========================
    @Override
    public void unsharePost(Long postId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Share share = shareRepository
                .findByOriginalPost_PostIdAndSharedBy_UserId(
                        postId,
                        user.getUserId()
                )
                .orElseThrow(() -> new RuntimeException("Share not found"));

        shareRepository.delete(share);
    }

    // =========================
    // TOGGLE SHARE / UNSHARE
    // =========================
    public boolean toggleShare(Long postId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<Share> existingShare =
                shareRepository.findByOriginalPost_PostIdAndSharedBy_UserId(
                        postId,
                        user.getUserId()
                );

        // already shared → unshare
        if(existingShare.isPresent()){
            shareRepository.delete(existingShare.get());
            return false;
        }

        // not shared → create share
        Share share = Share.builder()
                .originalPost(post)
                .sharedBy(user)
                .build();

        shareRepository.save(share);

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

        return shareRepository
                .findByOriginalPost_PostId(postId)
                .stream()
                .map(share -> share.getSharedBy().getUsername())
                .toList();
    }
}