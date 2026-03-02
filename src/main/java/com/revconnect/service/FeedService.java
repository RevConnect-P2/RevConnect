package com.revconnect.service;

import com.revconnect.dto.response.PostResponse;
import com.revconnect.common.PageResponse;
import com.revconnect.common.PageResponse;
public interface FeedService {

    PageResponse<PostResponse> getPersonalizedFeed(
            Long userId,
            int page,
            int size,
            String postType,
            String userType);

    PageResponse<PostResponse> getTrendingPosts(int page, int size);

    PageResponse<PostResponse> searchByHashtag(
            String hashtag,
            int page,
            int size);
}