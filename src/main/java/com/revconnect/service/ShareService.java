package com.revconnect.service;

public interface ShareService {

    void sharePost(Long postId, String email);

    void unsharePost(Long postId, String email);   // ADD THIS

    Long getShareCount(Long postId);               // keep as it is
}