package com.revconnect.service;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;

public interface ShareService {
    public void sharePost(Post post, User currentUser) ;

    }
import java.util.List;

public interface ShareService {

    void sharePost(Long postId, String email);

    void unsharePost(Long postId, String email);   // ADD THIS

    Long getShareCount(Long postId);               // keep as it is

    List<String> getUsersWhoShared(Long postId);
}
