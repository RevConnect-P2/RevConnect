package com.revconnect.dto.response;
import com.revconnect.enums.ProfileType;

import lombok.Data;

@Data
public class ProfileResponse {

    private Long userId;
    private String username;
    private String fullName;
    private String bio;
    private String profilePic;
    private String location;
    private String website;
    private String profileVisibility;

    private ProfileType profileType;
    private String category;
    private String externalLinks;
    private String businessAddress;
    private String contactInfo;

    private Long totalLikes;
    private Long totalComments;
    private Long totalShares;
    private Long totalPosts;
}