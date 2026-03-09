package com.revconnect.service.impl;

import com.revconnect.dto.response.PostResponse;
import com.revconnect.entity.Post;
import com.revconnect.entity.SavedPost;
import com.revconnect.entity.User;
import com.revconnect.exception.BadRequestException;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.mapper.PostMapper;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.SavedPostRepository;
import com.revconnect.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SavedPostServiceImplTest {

    @Mock
    private SavedPostRepository savedPostRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private SavedPostServiceImpl savedPostService;

    private User user;
    private Post post;
    private SavedPost savedPost;

    @Before
    public void setup() {

        user = new User();
        user.setUserId(1L);

        post = new Post();
        post.setPostId(10L);

        savedPost = new SavedPost();
        savedPost.setUser(user);
        savedPost.setPost(post);
    }

    // -------- savePost --------

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        savedPostService.savePost(1L,10L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfPostNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(10L)).thenReturn(Optional.empty());

        savedPostService.savePost(1L,10L);
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowIfPostAlreadySaved() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        when(savedPostRepository.findByUserAndPost(user,post))
                .thenReturn(Optional.of(savedPost));

        savedPostService.savePost(1L,10L);
    }

    @Test
    public void shouldSavePostSuccessfully() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        when(savedPostRepository.findByUserAndPost(user,post))
                .thenReturn(Optional.empty());

        savedPostService.savePost(1L,10L);

        verify(savedPostRepository).save(any(SavedPost.class));
    }

    // -------- unsavePost --------

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfUserNotFoundForUnsave() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        savedPostService.unsavePost(1L,10L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfPostNotFoundForUnsave() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(10L)).thenReturn(Optional.empty());

        savedPostService.unsavePost(1L,10L);
    }

    @Test
    public void shouldUnsavePostSuccessfully() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        savedPostService.unsavePost(1L,10L);

        verify(savedPostRepository).deleteByUserAndPost(user,post);
    }

    // -------- getSavedPosts --------

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfUserNotFoundForGetSavedPosts() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        savedPostService.getSavedPosts(1L);
    }

    @Test
    public void shouldReturnSavedPosts() {

        PostResponse response = new PostResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(savedPostRepository.findByUser(user))
                .thenReturn(List.of(savedPost));

        when(postMapper.toPostResponse(post,List.of(),List.of()))
                .thenReturn(response);

        List<PostResponse> result = savedPostService.getSavedPosts(1L);

        assertEquals(1,result.size());
    }
}