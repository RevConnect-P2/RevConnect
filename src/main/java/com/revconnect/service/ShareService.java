package com.revconnect.service;

import java.util.List;

public interface ShareService {

    // share post
    void sharePost(Long postId, String email);

    // unshare post
    void unsharePost(Long postId, String email);

    // NEW METHOD (Share / Unshare automatically)
    boolean toggleShare(Long postId, String email);

    // count shares
    Long getShareCount(Long postId);

    // list of users who shared
    List<String> getUsersWhoShared(Long postId);
}