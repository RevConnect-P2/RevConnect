package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.FollowService;
import com.revconnect.service.NotificationService;
import com.revconnect.service.PostService;
import com.revconnect.repository.UserProfileRepository;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private NotificationService notificationService;

    // ⭐ NEW MOCKS
    @Mock
    private FollowService followService;

    @Mock
    private ConnectionService connectionService;

    @Mock
    private Model model;

    @InjectMocks
    private DashboardController dashboardController;

    @Mock
    private UserProfileRepository userProfileRepository;


    private MockHttpSession session;

    @BeforeEach
    void setup() {
        session = new MockHttpSession();
    }

    // ================= USER NOT LOGGED IN =================

    @Test
    void dashboard_shouldRedirectToLogin_whenUserNotInSession() {

        String view = dashboardController.dashboard(session, model);

        assertEquals("redirect:/login", view);

        verifyNoInteractions(postService);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(followService);
        verifyNoInteractions(connectionService);
    }

    // ================= USER LOGGED IN =================

    @Test
    void dashboard_shouldLoadDashboard_whenUserExists() {

        User user = new User();
        user.setUserId(1L);

        session.setAttribute("loggedUser", user);

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.empty());

        when(notificationService.getUnreadCount(1L)).thenReturn(5L);
        when(postService.getGlobalFeed(1L)).thenReturn(List.of());
        when(postService.getTrendingHashtags()).thenReturn(List.of("#java", "#spring"));

        // ⭐ NEW STUBS
        when(followService.getFollowersCount(1L)).thenReturn(10L);
        when(followService.getFollowingCount(1L)).thenReturn(8L);
        when(connectionService.getConnectionsCount(1L)).thenReturn(3L);

        String view = dashboardController.dashboard(session, model);

        assertEquals("dashboard/dashboard", view);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("unreadCount", 5L);
        verify(model).addAttribute("followersCount", 10L);
        verify(model).addAttribute("followingCount", 8L);
        verify(model).addAttribute("connectionsCount", 3L);
        verify(model).addAttribute(eq("posts"), any());
        verify(model).addAttribute(eq("trendingTags"), any());
        verify(model).addAttribute("message", "Welcome to RevConnect Dashboard");

        verify(notificationService).getUnreadCount(1L);
        verify(postService).getGlobalFeed(1L);
        verify(postService).getTrendingHashtags();
        verify(followService).getFollowersCount(1L);
        verify(followService).getFollowingCount(1L);
        verify(connectionService).getConnectionsCount(1L);
    }
}