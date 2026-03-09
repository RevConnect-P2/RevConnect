package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.NotificationService;
import com.revconnect.service.PostService;

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

    @Mock
    private Model model;

    @InjectMocks
    private DashboardController dashboardController;

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
    }

    // ================= USER LOGGED IN =================

    @Test
    void dashboard_shouldLoadDashboard_whenUserExists() {

        User user = new User();
        user.setUserId(1L);

        session.setAttribute("loggedUser", user);

        when(notificationService.getUnreadCount(1L)).thenReturn(5L);
        when(postService.getGlobalFeed(1L)).thenReturn(List.of());
        when(postService.getTrendingHashtags()).thenReturn(List.of("#java", "#spring"));

        String view = dashboardController.dashboard(session, model);

        assertEquals("dashboard/dashboard", view);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("unreadCount", 5L);
        verify(model).addAttribute(eq("posts"), any());
        verify(model).addAttribute(eq("trendingTags"), any());
        verify(model).addAttribute("connectionsCount", 0);
        verify(model).addAttribute("followersCount", 0);
        verify(model).addAttribute("followingCount", 0);
        verify(model).addAttribute("message", "Welcome to RevConnect Dashboard");

        verify(notificationService).getUnreadCount(1L);
        verify(postService).getGlobalFeed(1L);
        verify(postService).getTrendingHashtags();
    }
}