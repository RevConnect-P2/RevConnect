package com.revconnect.dto.response;

import lombok.Data;

@Data
public class ProfileAnalyticsResponse {

    private Long totalLikes;
    private Long totalComments;
    private Long totalShares;
    private Long totalPosts;

}