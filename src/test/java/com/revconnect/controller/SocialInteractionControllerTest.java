package com.revconnect.controller;

import com.revconnect.repository.PostLikeRepository;
import com.revconnect.service.CommentService;
import com.revconnect.service.LikeService;
import com.revconnect.service.ShareService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SocialInteractionControllerTest {

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private LikeService likeService;

    @Mock
    private CommentService commentService;

    @Mock
    private ShareService shareService;

    @InjectMocks
    private SocialInteractionController controller;

    private MockMvc mockMvc;

    private Principal mockPrincipal() {
        return () -> "testUser";
    }

    private void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ================= LIKE =================

    @Test
    void testToggleLike_success() throws Exception {

        setup();

        when(likeService.toggleLike(1L, "testUser")).thenReturn(true);
        when(postLikeRepository.countByPost_PostId(1L)).thenReturn(5L);

        mockMvc.perform(post("/posts/1/like")
                        .principal(mockPrincipal()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").value(5));

        verify(likeService).toggleLike(1L, "testUser");
        verify(postLikeRepository).countByPost_PostId(1L);
    }

    @Test
    void testToggleLike_unauthenticated() {

        try {
            controller.toggleLike(1L, null);
        } catch (RuntimeException e) {
            assert(e.getMessage().equals("User not authenticated"));
        }
    }

    // ================= ADD COMMENT =================

    @Test
    void testAddComment_success() throws Exception {

        setup();

        mockMvc.perform(post("/posts/1/comments")
                        .principal(mockPrincipal())
                        .content("Nice post")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment added"));

        verify(commentService).addComment(1L, "testUser", "Nice post");
    }

    @Test
    void testAddComment_unauthenticated() {

        try {
            controller.addComment(1L, "Nice", null);
        } catch (RuntimeException e) {
            assert(e.getMessage().equals("User not authenticated"));
        }
    }

    // ================= GET COMMENTS =================

    @Test
    void testGetComments() throws Exception {

        setup();

        when(commentService.getCommentsByPostId(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/posts/1/comments"))
                .andExpect(status().isOk());

        verify(commentService).getCommentsByPostId(1L);
    }

    // ================= SHARE =================

    @Test
    void testToggleShare_success() throws Exception {

        setup();

        when(shareService.toggleShare(1L, "testUser")).thenReturn(true);
        when(shareService.getShareCount(1L)).thenReturn(3L);

        mockMvc.perform(post("/posts/1/share")
                        .principal(mockPrincipal()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shared").value(true))
                .andExpect(jsonPath("$.shareCount").value(3));

        verify(shareService).toggleShare(1L, "testUser");
        verify(shareService).getShareCount(1L);
    }

    @Test
    void testToggleShare_unauthenticated() {

        try {
            controller.toggleShare(1L, null);
        } catch (RuntimeException e) {
            assert(e.getMessage().equals("User not authenticated"));
        }
    }

    // ================= USERS WHO LIKED =================

    @Test
    void testGetUsersWhoLiked() throws Exception {

        setup();

        when(likeService.getUsersWhoLiked(1L))
                .thenReturn(List.of("user1", "user2"));

        mockMvc.perform(get("/posts/1/likes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("user1"))
                .andExpect(jsonPath("$[1]").value("user2"));

        verify(likeService).getUsersWhoLiked(1L);
    }
}