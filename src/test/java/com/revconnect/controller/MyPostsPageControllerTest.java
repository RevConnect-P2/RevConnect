package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.FollowService;
import com.revconnect.service.PostService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MyPostsPageControllerTest {

    private MyPostsPageController controller;

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

        controller = new MyPostsPageController(
                followService,
                connectionService,
                postService
        );

        session = new MockHttpSession();
        model = mock(Model.class);
    }

    // ===== USER NOT LOGGED IN =====

    @Test
    void myPostsPage_shouldRedirectToLogin_whenUserNotInSession() {

        String view = controller.myPostsPage(session, model);

        assertEquals("redirect:/login", view);
    }

    // ===== USER LOGGED IN =====

    @Test
    void myPostsPage_shouldLoadPage_whenUserExists() {

        User user = new User();
        user.setUserId(1L);

        session.setAttribute("loggedUser", user);

        when(followService.getFollowersCount(1L)).thenReturn(5L);
        when(followService.getFollowingCount(1L)).thenReturn(3L);
        when(connectionService.getConnectionsCount(1L)).thenReturn(2L);
        when(postService.getTrendingHashtags()).thenReturn(List.of());

        String view = controller.myPostsPage(session, model);

        assertEquals("posts/my-posts", view);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("followersCount", 5L);
        verify(model).addAttribute("followingCount", 3L);
        verify(model).addAttribute("connectionsCount", 2L);
        verify(model).addAttribute("trendingTags", List.of());
    }
}