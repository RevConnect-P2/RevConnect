package com.revconnect.service;

public interface ShareService {

    void sharePost(Long postId, String email);

    Long getShareCount(Long postId);
}