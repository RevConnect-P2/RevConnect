package com.revconnect.controller;

import com.revconnect.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MyPostsPageControllerTest {

    private MyPostsPageController controller;
    private MockHttpSession session;
    private Model model;

    @BeforeEach
    void setUp() {
        controller = new MyPostsPageController();
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
        session.setAttribute("loggedUser", user);

        String view = controller.myPostsPage(session, model);

        assertEquals("posts/my-posts", view);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("connectionsCount", 0);
        verify(model).addAttribute("followersCount", 0);
        verify(model).addAttribute("followingCount", 0);
    }
}