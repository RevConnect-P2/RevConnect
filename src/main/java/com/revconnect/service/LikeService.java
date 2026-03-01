package com.revconnect.service;

public interface LikeService {
    void likePost(Long postId, String username);

    void unlikePost(Long postId, String username);
}
