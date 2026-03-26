package com.revconnect.controller;

import com.revconnect.dto.request.ProfileCreateRequest;
import com.revconnect.dto.request.ProfileUpdateRequest;
import com.revconnect.dto.response.ProfileResponse;
import com.revconnect.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ✅ LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    // LOGGER OBJECT
    private static final Logger logger =
            LogManager.getLogger(ProfileController.class);

    private final ProfileService profileService;

    // ================= CREATE PROFILE =================
    @PostMapping("/{userId}")
    public ProfileResponse createProfile(
            @PathVariable Long userId,
            @RequestBody ProfileCreateRequest request
    ) {

        logger.info("Create profile request received for user {}", userId);

        ProfileResponse response =
                profileService.createProfile(userId, request);

        logger.info("Profile created successfully for user {}", userId);

        return response;
    }

    // ================= UPDATE PROFILE =================
    @PutMapping("/{userId}")
    public ProfileResponse updateProfile(
            @PathVariable Long userId,
            @RequestBody ProfileUpdateRequest request
    ) {

        logger.info("Update profile request for user {}", userId);

        ProfileResponse response =
                profileService.updateProfile(userId, request);

        logger.info("Profile updated successfully for user {}", userId);

        return response;
    }

    // ================= GET PROFILE =================
    @GetMapping("/{userId}")
    public ProfileResponse getProfile(@PathVariable Long userId) {

        logger.info("Fetching profile for user {}", userId);

        return profileService.getProfile(userId);
    }

    // ================= SEARCH PROFILES =================
    @GetMapping("/search")
    public List<ProfileResponse> searchProfiles(
            @RequestParam String query
    ) {

        logger.info("Searching profiles with query: {}", query);

        List<ProfileResponse> profiles =
                profileService.searchProfiles(query);

        logger.info("Found {} profiles for query {}", profiles.size(), query);

        return profiles;
    }
}