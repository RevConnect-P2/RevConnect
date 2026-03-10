package com.revconnect.controller;

import com.revconnect.dto.request.ProfileUpdateRequest;
import com.revconnect.dto.response.ProfileResponse;
import com.revconnect.enums.ProfileType;
import com.revconnect.service.FollowService;
import com.revconnect.service.ProfileService;
import com.revconnect.service.UserService;
import com.revconnect.service.PostService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PageControllerTest {

    @InjectMocks
    private PageController pageController;

    @Mock
    private ProfileService profileService;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @Mock
    private FollowService followService;

    @Mock
    private Model model;

    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("john");

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ================= PROFILE PAGE =================

    @Test
    void profilePage_shouldLoadProfile() {

        ProfileResponse profile = new ProfileResponse();
        profile.setProfileType(ProfileType.PERSONAL);

        when(userService.getUserIdByUsername("john")).thenReturn(1L);
        when(userService.getUsernameByUserId(1L)).thenReturn("john");
        when(profileService.getProfile(1L)).thenReturn(profile);
        when(postService.getPostsByUser(1L)).thenReturn(List.of());
        when(postService.countPostsByUser(1L)).thenReturn(2L);
        when(followService.getFollowersCount(1L)).thenReturn(5L);
        when(followService.getFollowingCount(1L)).thenReturn(3L);

        String view = pageController.profilePage(model);

        assertEquals("profile/profile", view);
    }

    @Test
    void profilePage_shouldLoadBusinessHours_forBusinessUser() {

        ProfileResponse profile = new ProfileResponse();
        profile.setProfileType(ProfileType.BUSINESS);

        when(userService.getUserIdByUsername("john")).thenReturn(1L);
        when(userService.getUsernameByUserId(1L)).thenReturn("john");
        when(profileService.getProfile(1L)).thenReturn(profile);
        when(postService.getPostsByUser(1L)).thenReturn(List.of());
        when(postService.countPostsByUser(1L)).thenReturn(3L);
        when(profileService.getBusinessHours(1L)).thenReturn(List.of());
        when(followService.getFollowersCount(1L)).thenReturn(5L);
        when(followService.getFollowingCount(1L)).thenReturn(3L);

        String view = pageController.profilePage(model);

        assertEquals("profile/profile", view);
    }

    // ================= EDIT PROFILE =================

    @Test
    void editProfilePage_shouldLoadEditPage() {

        ProfileResponse profile = new ProfileResponse();

        when(userService.getUserIdByUsername("john")).thenReturn(1L);
        when(profileService.getProfile(1L)).thenReturn(profile);

        String view = pageController.editProfilePage(model);

        assertEquals("profile/edit-profile", view);
    }

    // ================= UPDATE PROFILE =================

    @Test
    void updateProfile_shouldRedirect() {

        ProfileUpdateRequest req = new ProfileUpdateRequest();

        when(userService.getUserIdByUsername("john")).thenReturn(1L);

        String view = pageController.updateProfile(req);

        assertEquals("redirect:/profile", view);

        verify(profileService).updateProfile(eq(1L), eq(req));
    }

    // ================= BUSINESS HOURS PAGE =================

    @Test
    void businessHoursPage_shouldRedirect_whenNotBusiness() {

        ProfileResponse profile = new ProfileResponse();
        profile.setProfileType(ProfileType.PERSONAL);

        when(userService.getUserIdByUsername("john")).thenReturn(1L);
        when(profileService.getProfile(1L)).thenReturn(profile);

        String view = pageController.businessHoursPage(model);

        assertEquals("redirect:/profile", view);
    }

    @Test
    void businessHoursPage_shouldLoadPage_whenBusiness() {

        ProfileResponse profile = new ProfileResponse();
        profile.setProfileType(ProfileType.BUSINESS);

        when(userService.getUserIdByUsername("john")).thenReturn(1L);
        when(profileService.getProfile(1L)).thenReturn(profile);

        String view = pageController.businessHoursPage(model);

        assertEquals("profile/business-hours", view);
    }

    // ================= SAVE BUSINESS HOURS =================

    @Test
    void saveBusinessHours_shouldRedirectOnSuccess() {

        when(userService.getUserIdByUsername("john")).thenReturn(1L);

        String view = pageController.saveBusinessHours(
                List.of("Monday"),
                List.of("09:00"),
                List.of("18:00"),
                null,
                model
        );

        assertEquals("redirect:/profile", view);
    }

    @Test
    void saveBusinessHours_shouldReturnPageOnError() {

        when(userService.getUserIdByUsername("john")).thenReturn(1L);

        String view = pageController.saveBusinessHours(
                List.of("Monday"),
                List.of("INVALID"),
                List.of("18:00"),
                null,
                model
        );

        assertEquals("profile/business-hours", view);
    }
}