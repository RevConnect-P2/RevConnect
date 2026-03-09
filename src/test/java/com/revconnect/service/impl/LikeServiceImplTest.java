package com.revconnect.service.impl;

import com.revconnect.entity.Post;
import com.revconnect.entity.PostLike;
import com.revconnect.entity.User;
import com.revconnect.repository.PostLikeRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class LikeServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LikeServiceImpl likeService;

    private User user;
    private Post post;

    @Before
    public void setup() {

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@mail.com");

        User postOwner = new User();
        postOwner.setUserId(2L);

        post = new Post();
        post.setPostId(10L);
        post.setUser(postOwner);   // ⭐ IMPORTANT FIX
    }

    @Test
    public void shouldLikePostSuccessfully() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(post));

        when(postLikeRepository
                .findByPost_PostIdAndUser_Email(10L, "test@mail.com"))
                .thenReturn(Optional.empty());

        likeService.likePost(10L, "test@mail.com");

        verify(postLikeRepository, times(1))
                .save(any(PostLike.class));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfDuplicateLike() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(post));

        when(postLikeRepository
                .findByPost_PostIdAndUser_Email(10L, "test@mail.com"))
                .thenReturn(Optional.of(new PostLike()));

        likeService.likePost(10L, "test@mail.com");
    }


    @Test
    public void shouldUnlikeSuccessfully() {

        User user = new User();
        user.setEmail("test@mail.com");

        PostLike like = new PostLike();

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(postLikeRepository
                .findByPost_PostIdAndUser_Email(10L, "test@mail.com"))
                .thenReturn(Optional.of(like));

        likeService.unlikePost(10L, "test@mail.com");

        verify(postLikeRepository, times(1)).delete(like);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfUnlikeWithoutLike() {

        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(postLikeRepository
                .findByPost_PostIdAndUser_Email(10L, "test@mail.com"))
                .thenReturn(Optional.empty());

        likeService.unlikePost(10L, "test@mail.com");
    }
}