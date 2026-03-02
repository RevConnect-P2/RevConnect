package com.revconnect.controller;

import com.revconnect.dto.request.ProfileCreateRequest;
import com.revconnect.dto.request.ProfileUpdateRequest;
import com.revconnect.dto.response.ProfileResponse;
import com.revconnect.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/{userId}")
    public ProfileResponse createProfile(
            @PathVariable Long userId,
            @RequestBody ProfileCreateRequest request) {
        return profileService.createProfile(userId, request);
    }

    @PutMapping("/{userId}")
    public ProfileResponse updateProfile(
            @PathVariable Long userId,
            @RequestBody ProfileUpdateRequest request) {
        return profileService.updateProfile(userId, request);
    }

    @GetMapping("/{userId}")
    public ProfileResponse getProfile(@PathVariable Long userId) {
        return profileService.getProfile(userId);
    }

    @GetMapping("/search")
    public List<ProfileResponse> searchProfiles(
            @RequestParam String query) {
        return profileService.searchProfiles(query);
    }
}