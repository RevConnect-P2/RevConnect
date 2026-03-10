package com.revconnect.service.impl;

import com.revconnect.entity.Post;
import com.revconnect.entity.Share;
import com.revconnect.entity.User;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.ShareRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.NotificationService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class ShareServiceImplTest {

    @Mock
    private ShareRepository shareRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ShareServiceImpl shareService;

    private User user;
    private Post post;

    @Before
    public void setup() {

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@mail.com");

        post = new Post();
        post.setPostId(10L);

        User owner = new User();
        owner.setUserId(2L);
        post.setUser(owner);
    }

    @Test
    public void shouldSharePostSuccessfully() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(post));

        when(shareRepository
                .findByOriginalPost_PostIdAndSharedBy_UserId(10L, 1L))
                .thenReturn(Optional.empty());

        shareService.sharePost(10L, "test@mail.com");

        verify(shareRepository, times(1))
                .save(any(Share.class));
    }

    @Test
    public void shouldNotShareIfAlreadyShared() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(post));

        when(shareRepository
                .findByOriginalPost_PostIdAndSharedBy_UserId(10L, 1L))
                .thenReturn(Optional.of(new Share()));

        shareService.sharePost(10L, "test@mail.com");

        verify(shareRepository, never())
                .save(any(Share.class));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfUserNotFound() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        shareService.sharePost(10L, "test@mail.com");
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfPostNotFound() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(10L))
                .thenReturn(Optional.empty());

        shareService.sharePost(10L, "test@mail.com");
    }

    @Test
    public void shouldReturnShareCount() {

        when(shareRepository.countByOriginalPost_PostId(10L))
                .thenReturn(5L);

        Long count = shareService.getShareCount(10L);

        assertEquals(Long.valueOf(5), count);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfUnshareNotFound() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(shareRepository
                .findByOriginalPost_PostIdAndSharedBy_UserId(10L, 1L))
                .thenReturn(Optional.empty());

        shareService.unsharePost(10L, "test@mail.com");
    }

    @Test
    public void shouldToggleShareAndRemoveExistingShare() {

        Share share = new Share();

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(post));

        when(shareRepository
                .findByOriginalPost_PostIdAndSharedBy_UserId(10L, 1L))
                .thenReturn(Optional.of(share));

        boolean result = shareService.toggleShare(10L, "test@mail.com");

        verify(shareRepository).delete(share);

        assertFalse(result);
    }

    @Test
    public void shouldToggleShareAndCreateShare() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(post));

        when(shareRepository
                .findByOriginalPost_PostIdAndSharedBy_UserId(10L, 1L))
                .thenReturn(Optional.empty());

        boolean result = shareService.toggleShare(10L, "test@mail.com");

        verify(shareRepository).save(any(Share.class));

        assertTrue(result);
    }

    @Test
    public void shouldUnshareSuccessfully() {

        Share share = new Share();

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(shareRepository
                .findByOriginalPost_PostIdAndSharedBy_UserId(10L, 1L))
                .thenReturn(Optional.of(share));

        shareService.unsharePost(10L, "test@mail.com");

        verify(shareRepository).delete(share);
    }

    @Test
    public void shouldReturnUsersWhoShared() {

        when(shareRepository.findUsernamesWhoShared(10L))
                .thenReturn(java.util.List.of("alice", "bob"));

        java.util.List<String> users =
                shareService.getUsersWhoShared(10L);

        assertEquals(2, users.size());
    }

    @Test
    public void shouldNotSendNotificationForSelfShare() {

        user.setUserId(1L);

        post.setUser(user); // same user sharing own post

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(post));

        when(shareRepository
                .findByOriginalPost_PostIdAndSharedBy_UserId(10L, 1L))
                .thenReturn(Optional.empty());

        shareService.sharePost(10L, "test@mail.com");

        verify(notificationService, never())
                .createNotification(any(), any(), any(), any(), any());
    }


}