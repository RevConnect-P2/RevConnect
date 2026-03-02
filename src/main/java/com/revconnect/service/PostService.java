package com.revconnect.service;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;

public interface PostService {
    void createPost(Post post, User user);
}
