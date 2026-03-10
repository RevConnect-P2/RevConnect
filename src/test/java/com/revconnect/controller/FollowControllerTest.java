package com.revconnect.controller;

import com.revconnect.entity.Follow;
import com.revconnect.service.FollowService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FollowControllerTest {

    @Mock
    private FollowService followService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {

        FollowController controller =
                new FollowController(followService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    // ================= FOLLOW USER =================

    @Test
    void shouldFollowUser() throws Exception {

        mockMvc.perform(
                post("/follow/1/2")
        )
        .andExpect(status().isOk())
        .andExpect(content().string("User followed successfully"));

        verify(followService)
                .followUser(1L, 2L);
    }

    // ================= UNFOLLOW USER =================

    @Test
    void shouldUnfollowUser() throws Exception {

        mockMvc.perform(
                delete("/follow/1/2")
        )
        .andExpect(status().isOk())
        .andExpect(content().string("User unfollowed successfully"));

        verify(followService)
                .unfollowUser(1L, 2L);
    }

    // ================= TOGGLE FOLLOW =================

    @Test
    void shouldToggleFollowTrue() throws Exception {

        when(followService.toggleFollow(1L, 2L))
                .thenReturn(true);

        mockMvc.perform(
                post("/follow/toggle/1/2")
        )
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
    }

    @Test
    void shouldToggleFollowFalse() throws Exception {

        when(followService.toggleFollow(1L, 2L))
                .thenReturn(false);

        mockMvc.perform(
                post("/follow/toggle/1/2")
        )
        .andExpect(status().isOk())
        .andExpect(content().string("false"));
    }

    // ================= FOLLOWERS COUNT =================

    @Test
    void shouldReturnFollowersCount() throws Exception {

        when(followService.getFollowersCount(1L))
                .thenReturn(10L);

        mockMvc.perform(
                get("/follow/followers/count/1")
        )
        .andExpect(status().isOk())
        .andExpect(content().string("10"));

        verify(followService)
                .getFollowersCount(1L);
    }

    // ================= FOLLOWING COUNT =================

    @Test
    void shouldReturnFollowingCount() throws Exception {

        when(followService.getFollowingCount(1L))
                .thenReturn(7L);

        mockMvc.perform(
                get("/follow/following/count/1")
        )
        .andExpect(status().isOk())
        .andExpect(content().string("7"));

        verify(followService)
                .getFollowingCount(1L);
    }

    // ================= FOLLOWERS LIST =================

    @Test
    void shouldReturnFollowersList() throws Exception {

        Follow follow = new Follow();

        when(followService.getFollowers(1L))
                .thenReturn(List.of(follow));

        mockMvc.perform(
                get("/follow/followers/1")
        )
        .andExpect(status().isOk());

        verify(followService)
                .getFollowers(1L);
    }

    // ================= FOLLOWING LIST =================

    @Test
    void shouldReturnFollowingList() throws Exception {

        Follow follow = new Follow();

        when(followService.getFollowing(1L))
                .thenReturn(List.of(follow));

        mockMvc.perform(
                get("/follow/following/1")
        )
        .andExpect(status().isOk());

        verify(followService)
                .getFollowing(1L);
    }
}