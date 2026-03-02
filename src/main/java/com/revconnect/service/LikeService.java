package com.revconnect.service;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;

public interface LikeService {
    void likePost(Post post, User currentUser);

}
