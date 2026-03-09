package com.revconnect.controller;

import com.revconnect.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SavedPostPageControllerTest {

    private SavedPostPageController controller;

    private MockHttpSession session;

    private Model model;

    @BeforeEach
    void setUp() {
        controller = new SavedPostPageController();
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
        session.setAttribute("loggedUser", user);

        String view = controller.savedPostsPage(session, model);

        assertEquals("posts/saved-posts", view);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("connectionsCount", 0);
        verify(model).addAttribute("followersCount", 0);
        verify(model).addAttribute("followingCount", 0);
    }
}