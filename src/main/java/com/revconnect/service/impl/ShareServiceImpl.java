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

        // If already shared → just return silently
        if (shareRepository
                .findByOriginalPost_PostIdAndSharedBy_Email(postId, email)
                .isPresent()) {
            return;   // ✅ DO NOTHING (no error)
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


    @Override
    public List<String> getUsersWhoShared(Long postId) {

        return shareRepository
                .findByOriginalPost_PostId(postId)
                .stream()
                .map(share -> share.getSharedBy().getUsername())
                .toList();
    }
}