package com.revconnect.dto.request;
import com.revconnect.enums.ProfileType;

import lombok.Data;

@Data
public class ProfileUpdateRequest {

    private String fullName;
    private String bio;
    private String profilePic;
    private String location;
    private String website;
    private String profileVisibility;

    private ProfileType profileType;

    // CREATOR
    private String category;
    private String externalLinks;

    // BUSINESS
    private String businessAddress;
    private String contactInfo;
}