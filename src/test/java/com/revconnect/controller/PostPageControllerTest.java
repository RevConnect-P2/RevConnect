package com.revconnect.controller;

import com.revconnect.dto.response.PostResponse;
import com.revconnect.entity.User;
import com.revconnect.service.PostService;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PostPageControllerTest {

    private PostPageController controller;
    private PostService postService;

    private MockHttpSession session;
    private Model model;

    @BeforeEach
    void setUp() {
        postService = mock(PostService.class);
        controller = new PostPageController(postService);
        session = new MockHttpSession();
        model = mock(Model.class);
    }

    // ================= CREATE PAGE =================

    @Test
    void showCreatePostPage_shouldRedirect_whenUserNotLoggedIn() {

        String view = controller.showCreatePostPage(session, model);

        assertEquals("redirect:/login", view);
    }

    @Test
    void showCreatePostPage_shouldLoadPage_whenUserLoggedIn() {

        User user = new User();
        session.setAttribute("loggedUser", user);

        String view = controller.showCreatePostPage(session, model);

        assertEquals("posts/create-post", view);

        verify(model).addAttribute(eq("userType"), any());
    }

    // ================= EDIT PAGE =================

    @Test
    void showEditPostPage_shouldRedirect_whenUserNotLoggedIn() {

        String view = controller.showEditPostPage(1L, session, model);

        assertEquals("redirect:/login", view);
    }

    @Test
    void showEditPostPage_shouldRedirectDashboard_whenNotOwner() {

        User loggedUser = new User();
        loggedUser.setUserId(1L);

        session.setAttribute("loggedUser", loggedUser);

        PostResponse post = new PostResponse();
        post.setUserId(2L);

        when(postService.getPostById(1L)).thenReturn(post);

        String view = controller.showEditPostPage(1L, session, model);

        assertEquals("redirect:/dashboard", view);
    }

    @Test
    void showEditPostPage_shouldLoadEditPage_whenOwner() {

        User loggedUser = new User();
        loggedUser.setUserId(1L);

        session.setAttribute("loggedUser", loggedUser);

        PostResponse post = new PostResponse();
        post.setUserId(1L);

        when(postService.getPostById(1L)).thenReturn(post);

        String view = controller.showEditPostPage(1L, session, model);

        assertEquals("posts/create-post", view);

        verify(model).addAttribute("user", loggedUser);
        verify(model).addAttribute(eq("userType"), any());
        verify(model).addAttribute("post", post);
        verify(model).addAttribute("isEdit", true);
    }
}