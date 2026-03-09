package com.revconnect.controller;

import com.revconnect.dto.request.ProfileCreateRequest;
import com.revconnect.dto.request.ProfileUpdateRequest;
import com.revconnect.dto.response.ProfileResponse;
import com.revconnect.service.ProfileService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    // ================= CREATE PROFILE =================
    @Test
    void shouldCreateProfile() {

        Long userId = 1L;

        ProfileCreateRequest request = new ProfileCreateRequest();

        ProfileResponse response = new ProfileResponse();
        response.setUserId(userId);

        when(profileService.createProfile(userId, request)).thenReturn(response);

        ProfileResponse result = profileController.createProfile(userId, request);

        assertEquals(response, result);

        verify(profileService).createProfile(userId, request);
    }

    // ================= UPDATE PROFILE =================
    @Test
    void shouldUpdateProfile() {

        Long userId = 1L;

        ProfileUpdateRequest request = new ProfileUpdateRequest();

        ProfileResponse response = new ProfileResponse();
        response.setUserId(userId);

        when(profileService.updateProfile(userId, request)).thenReturn(response);

        ProfileResponse result = profileController.updateProfile(userId, request);

        assertEquals(response, result);

        verify(profileService).updateProfile(userId, request);
    }

    // ================= GET PROFILE =================
    @Test
    void shouldGetProfile() {

        Long userId = 1L;

        ProfileResponse response = new ProfileResponse();
        response.setUserId(userId);

        when(profileService.getProfile(userId)).thenReturn(response);

        ProfileResponse result = profileController.getProfile(userId);

        assertEquals(response, result);

        verify(profileService).getProfile(userId);
    }

    // ================= SEARCH PROFILES =================
    @Test
    void shouldSearchProfiles() {

        String query = "john";

        ProfileResponse profile = new ProfileResponse();

        when(profileService.searchProfiles(query)).thenReturn(List.of(profile));

        List<ProfileResponse> result = profileController.searchProfiles(query);

        assertEquals(1, result.size());

        verify(profileService).searchProfiles(query);
    }
}