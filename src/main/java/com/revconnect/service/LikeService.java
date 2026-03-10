package com.revconnect.service;

import java.util.List;

public interface LikeService {

    void likePost(Long postId, String username);

    void unlikePost(Long postId, String username);

    boolean toggleLike(Long postId, String username);

    List<String> getUsersWhoLiked(Long postId);
}