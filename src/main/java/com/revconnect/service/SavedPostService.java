package com.revconnect.service;

import com.revconnect.dto.response.PostResponse;

import java.util.List;

public interface SavedPostService {

    void savePost(Long userId, Long postId);

    void unsavePost(Long userId, Long postId);

    List<PostResponse> getSavedPosts(Long userId);
}