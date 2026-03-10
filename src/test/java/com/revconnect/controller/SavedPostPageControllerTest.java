package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.FollowService;
import com.revconnect.service.PostService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SavedPostPageControllerTest {

    private SavedPostPageController controller;

    private FollowService followService;
    private ConnectionService connectionService;
    private PostService postService;

    private MockHttpSession session;
    private Model model;

    @BeforeEach
    void setUp() {

        followService = mock(FollowService.class);
        connectionService = mock(ConnectionService.class);
        postService = mock(PostService.class);

        controller = new SavedPostPageController(
                followService,
                connectionService,
                postService
        );

        session = new MockHttpSession();
        model = mock(Model.class);
    }

    // ===== USER NOT LOGGED IN =====

    @Test
    void savedPostsPage_shouldRedirectToLogin_whenUserNotInSession() {

        String view = controller.savedPostsPage(session, model);

        assertEquals("redirect:/login", view);
    }

    // ===== USER LOGGED IN =====

    @Test
    void savedPostsPage_shouldLoadSavedPosts_whenUserExists() {

        User user = new User();
        user.setUserId(1L);

        session.setAttribute("loggedUser", user);

        when(followService.getFollowersCount(1L)).thenReturn(0L);
        when(followService.getFollowingCount(1L)).thenReturn(0L);
        when(connectionService.getConnectionsCount(1L)).thenReturn(0L);

        String view = controller.savedPostsPage(session, model);

        assertEquals("posts/saved-posts", view);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("connectionsCount", 0L);
        verify(model).addAttribute("followersCount", 0L);
        verify(model).addAttribute("followingCount", 0L);
    }
}