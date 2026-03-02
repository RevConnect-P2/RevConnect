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

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final ShareRepository shareRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public void sharePost(Long postId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check duplicate share
        if (shareRepository
                .findByOriginalPost_PostIdAndSharedBy_Email(postId, email)
                .isPresent()) {

            throw new RuntimeException("You have already shared this post");
        }

        Share share = Share.builder()
                .originalPost(post)
                .sharedBy(user)
                .build();

        shareRepository.save(share);
    }

    @Override
    public void unsharePost(Long postId, String email) {

        Share share = shareRepository
                .findByOriginalPost_PostIdAndSharedBy_Email(postId, email)
                .orElseThrow(() -> new RuntimeException("Share not found"));

        shareRepository.delete(share);
    }

    @Override
    public Long getShareCount(Long postId) {

        return shareRepository.countByOriginalPost_PostId(postId);
    }
}