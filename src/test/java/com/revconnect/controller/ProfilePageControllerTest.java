package com.revconnect.controller;

import com.revconnect.entity.Follow;
import com.revconnect.entity.User;
import com.revconnect.entity.UserProfile;
import com.revconnect.enums.ConnectionStatus;
import com.revconnect.enums.ProfileType;
import com.revconnect.repository.UserProfileRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.FollowService;

import com.revconnect.service.ProfileService;
import jakarta.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ProfilePageControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private FollowService followService;

    @Mock
    private ConnectionService connectionService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private ProfilePageController controller;

    @Mock
    private ProfileService profileService;

    private User user;
    private UserProfile profile;

    @Before
    public void setup() {

        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);
        user.setUsername("john");

        profile = new UserProfile();
        profile.setUser(user);
    }

    // =========================
    // VIEW PROFILE (LOGGED USER)
    // =========================
    @Test
    public void shouldViewProfileWhenLoggedIn() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(followService.getFollowersCount(1L)).thenReturn(5L);
        when(followService.getFollowingCount(1L)).thenReturn(3L);

        when(session.getAttribute("loggedUser")).thenReturn(user);

        when(followService.isFollowing(1L, 1L)).thenReturn(true);

        when(connectionService.getConnectionStatus(1L, 1L))
                .thenReturn(ConnectionStatus.ACCEPTED);

        String view = controller.viewProfile("john", model, session);

        assertEquals("profile/public-profile", view);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("profile", profile);
        verify(model).addAttribute("followers", 5L);
        verify(model).addAttribute("following", 3L);
        verify(model).addAttribute("isFollowing", true);
        verify(model).addAttribute("connectionStatus", ConnectionStatus.ACCEPTED);
    }

    // =========================
    // VIEW PROFILE (NOT LOGGED IN)
    // =========================
    @Test
    public void shouldViewProfileWithoutLoggedUser() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(followService.getFollowersCount(1L)).thenReturn(5L);
        when(followService.getFollowingCount(1L)).thenReturn(3L);

        when(session.getAttribute("loggedUser")).thenReturn(null);

        String view = controller.viewProfile("john", model, session);

        assertEquals("profile/public-profile", view);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("profile", profile);
    }

    // =========================
    // VIEW FOLLOWERS
    // =========================
    @Test
    public void shouldViewFollowers() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        Follow follow = new Follow();
        follow.setFollower(user);
        follow.setFollowing(user);

        when(followService.getFollowers(1L))
                .thenReturn(List.of(follow));

        String view = controller.viewFollowers("john", model);

        assertEquals("profile/followers", view);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("followersList", List.of(follow));
    }

    // =========================
    // VIEW FOLLOWING
    // =========================
    @Test
    public void shouldViewFollowing() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        Follow follow = new Follow();
        follow.setFollower(user);
        follow.setFollowing(user);

        when(followService.getFollowing(1L))
                .thenReturn(List.of(follow));

        String view = controller.viewFollowing("john", model);

        assertEquals("profile/following", view);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("followingList", List.of(follow));
    }

    // =========================
// BUSINESS PROFILE HOURS
// =========================
    @Test
    public void shouldLoadBusinessHoursForBusinessProfile() {

        profile.setProfileType(ProfileType.BUSINESS);

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(profileService.getBusinessHours(1L))
                .thenReturn(List.of());

        when(followService.getFollowersCount(1L)).thenReturn(2L);
        when(followService.getFollowingCount(1L)).thenReturn(1L);

        when(session.getAttribute("loggedUser")).thenReturn(null);

        String view = controller.viewProfile("john", model, session);

        assertEquals("profile/public-profile", view);

        verify(model).addAttribute(eq("businessHours"), any());
    }

    // =========================
// PUBLIC PROFILE VISIBILITY
// =========================
    @Test
    public void shouldAllowViewingPostsWhenProfilePublic() {

        profile.setProfileVisibility("PUBLIC");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(followService.getFollowersCount(1L)).thenReturn(1L);
        when(followService.getFollowingCount(1L)).thenReturn(1L);

        when(session.getAttribute("loggedUser")).thenReturn(user);

        when(followService.isFollowing(1L,1L)).thenReturn(false);
        when(connectionService.getConnectionStatus(1L,1L))
                .thenReturn(ConnectionStatus.PENDING);

        String view = controller.viewProfile("john", model, session);

        assertEquals("profile/public-profile", view);

        verify(model).addAttribute("canViewPosts", true);
    }

    // =========================
// PRIVATE PROFILE + CONNECTION
// =========================
    @Test
    public void shouldAllowViewingPostsWhenPrivateButConnected() {

        profile.setProfileVisibility("PRIVATE");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        when(followService.getFollowersCount(1L)).thenReturn(1L);
        when(followService.getFollowingCount(1L)).thenReturn(1L);

        when(session.getAttribute("loggedUser")).thenReturn(user);

        when(followService.isFollowing(1L,1L)).thenReturn(true);

        when(connectionService.getConnectionStatus(1L,1L))
                .thenReturn(ConnectionStatus.ACCEPTED);

        String view = controller.viewProfile("john", model, session);

        assertEquals("profile/public-profile", view);

        verify(model).addAttribute("canViewPosts", true);
    }

    // =========================
// PROFILE NULL
// =========================
    @Test
    public void shouldHandleNullProfile() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.empty());

        when(followService.getFollowersCount(1L)).thenReturn(0L);
        when(followService.getFollowingCount(1L)).thenReturn(0L);

        when(session.getAttribute("loggedUser")).thenReturn(null);

        String view = controller.viewProfile("john", model, session);

        assertEquals("profile/public-profile", view);

        verify(model).addAttribute("profile", null);
    }

    // =========================
// USER NOT FOUND
// =========================
    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        controller.viewProfile("john", model, session);
    }

}