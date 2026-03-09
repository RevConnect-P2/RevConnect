package com.revconnect.controller;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.response.PostResponse;
import com.revconnect.service.PostService;
import com.revconnect.service.SavedPostService;
import com.revconnect.service.ShareService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private SavedPostService savedPostService;

    @Mock
    private ShareService shareService;

    @InjectMocks
    private PostController postController;

    // ================= CREATE POST =================
    @Test
    void shouldCreatePost() {

        Long userId = 1L;
        PostCreateRequest request = new PostCreateRequest();

        PostResponse response = new PostResponse();

        when(postService.createPost(userId, request)).thenReturn(response);

        ResponseEntity<PostResponse> result =
                postController.createPost(userId, request);

        assertEquals(response, result.getBody());

        verify(postService).createPost(userId, request);
    }

    // ================= UPDATE POST =================
    @Test
    void shouldUpdatePost() {

        Long postId = 1L;
        Long userId = 2L;

        PostCreateRequest request = new PostCreateRequest();

        PostResponse response = new PostResponse();

        when(postService.updatePost(postId, userId, request)).thenReturn(response);

        ResponseEntity<PostResponse> result =
                postController.updatePost(postId, userId, request);

        assertEquals(response, result.getBody());

        verify(postService).updatePost(postId, userId, request);
    }

    // ================= DELETE POST =================
    @Test
    void shouldDeletePost() {

        Long postId = 1L;
        Long userId = 2L;

        ResponseEntity<String> result =
                postController.deletePost(postId, userId);

        assertEquals("Post deleted successfully", result.getBody());

        verify(postService).deletePost(postId, userId);
    }

    // ================= GET POST BY ID =================
    @Test
    void shouldGetPostById() {

        Long postId = 1L;

        PostResponse response = new PostResponse();

        when(postService.getPostById(postId)).thenReturn(response);

        ResponseEntity<PostResponse> result =
                postController.getPostById(postId);

        assertEquals(response, result.getBody());

        verify(postService).getPostById(postId);
    }

    // ================= GET POSTS BY USER =================
    @Test
    void shouldGetPostsByUser() {

        Long userId = 1L;

        PostResponse post = new PostResponse();

        when(postService.getPostsByUser(userId))
                .thenReturn(List.of(post));

        ResponseEntity<List<PostResponse>> result =
                postController.getPostsByUser(userId);

        assertEquals(1, result.getBody().size());

        verify(postService).getPostsByUser(userId);
    }

    // ================= GLOBAL FEED =================
    @Test
    void shouldGetGlobalFeed() {

        Long viewerId = 1L;

        PostResponse post = new PostResponse();

        when(postService.getGlobalFeed(viewerId))
                .thenReturn(List.of(post));

        ResponseEntity<List<PostResponse>> result =
                postController.getGlobalFeed(viewerId);

        assertEquals(1, result.getBody().size());

        verify(postService).getGlobalFeed(viewerId);
    }

    // ================= PIN POST =================
    @Test
    void shouldPinPost() {

        Long postId = 1L;
        Long userId = 2L;

        PostResponse response = new PostResponse();

        when(postService.pinPost(postId, userId)).thenReturn(response);

        ResponseEntity<PostResponse> result =
                postController.pinPost(postId, userId);

        assertEquals(response, result.getBody());

        verify(postService).pinPost(postId, userId);
    }

    // ================= UNPIN POST =================
    @Test
    void shouldUnpinPost() {

        Long postId = 1L;
        Long userId = 2L;

        PostResponse response = new PostResponse();

        when(postService.unpinPost(postId, userId)).thenReturn(response);

        ResponseEntity<PostResponse> result =
                postController.unpinPost(postId, userId);

        assertEquals(response, result.getBody());

        verify(postService).unpinPost(postId, userId);
    }

    // ================= SAVE POST =================
    @Test
    void shouldSavePost() {

        Long postId = 1L;
        Long userId = 2L;

        ResponseEntity<String> result =
                postController.savePost(postId, userId);

        assertEquals("Post saved successfully", result.getBody());

        verify(savedPostService).savePost(userId, postId);
    }

    // ================= UNSAVE POST =================
    @Test
    void shouldUnsavePost() {

        Long postId = 1L;
        Long userId = 2L;

        ResponseEntity<String> result =
                postController.unsavePost(postId, userId);

        assertEquals("Post unsaved successfully", result.getBody());

        verify(savedPostService).unsavePost(userId, postId);
    }

    // ================= GET SAVED POSTS =================
    @Test
    void shouldGetSavedPosts() {

        Long userId = 1L;

        PostResponse post = new PostResponse();

        when(savedPostService.getSavedPosts(userId))
                .thenReturn(List.of(post));

        ResponseEntity<List<PostResponse>> result =
                postController.getSavedPosts(userId);

        assertEquals(1, result.getBody().size());

        verify(savedPostService).getSavedPosts(userId);
    }
}