package com.revconnect.service;

import com.revconnect.dto.response.ProfileAnalyticsResponse;
import com.revconnect.entity.Post;

public interface AnalyticsService {

    void incrementLikes(Post post);

    void incrementComments(Post post);

    void incrementShares(Post post);
    void decrementLikes(Post post);

    void decrementComments(Post post);

    void decrementShares(Post post);
    ProfileAnalyticsResponse getUserAnalytics(Long userId);


}