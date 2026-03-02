package com.revconnect.service;

import com.revconnect.entity.Post;
import com.revconnect.entity.User;

public interface ShareService {
    public void sharePost(Post post, User currentUser) ;

    }
