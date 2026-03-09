package com.revconnect.service.impl;

import com.revconnect.entity.Follow;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.FollowRepository;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.NotificationService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FollowServiceImplTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FollowServiceImpl followService;

    private User follower;
    private User following;

    @Before
    public void setup() {

        follower = new User();
        follower.setUserId(1L);

        following = new User();
        following.setUserId(2L);
    }

    // ---------------- FOLLOW USER ----------------

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfFollowYourself() {
        followService.followUser(1L,1L);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfFollowerNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        followService.followUser(1L,2L);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfFollowingUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        followService.followUser(1L,2L);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfAlreadyFollowing() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));

        when(followRepository.findByFollowerAndFollowing(follower,following))
                .thenReturn(Optional.of(new Follow()));

        followService.followUser(1L,2L);
    }

    @Test
    public void shouldFollowSuccessfully() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));

        when(followRepository.findByFollowerAndFollowing(follower,following))
                .thenReturn(Optional.empty());

        followService.followUser(1L,2L);

        verify(followRepository).save(any(Follow.class));
        verify(notificationService).createNotification(
                1L,2L,null,NotificationType.FOLLOW,null
        );
    }

    // ---------------- UNFOLLOW ----------------

    @Test
    public void shouldUnfollowSuccessfully() {

        Follow follow = new Follow();

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));

        when(followRepository.findByFollowerAndFollowing(follower,following))
                .thenReturn(Optional.of(follow));

        followService.unfollowUser(1L,2L);

        verify(followRepository).delete(follow);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfFollowRelationshipNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));

        when(followRepository.findByFollowerAndFollowing(follower,following))
                .thenReturn(Optional.empty());

        followService.unfollowUser(1L,2L);
    }

    // ---------------- TOGGLE FOLLOW ----------------

    @Test
    public void shouldToggleUnfollow() {

        Follow follow = new Follow();

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));

        when(followRepository.findByFollowerAndFollowing(follower,following))
                .thenReturn(Optional.of(follow));

        boolean result = followService.toggleFollow(1L,2L);

        assertFalse(result);

        verify(followRepository).delete(follow);
    }

    @Test
    public void shouldToggleFollow() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(following));

        when(followRepository.findByFollowerAndFollowing(follower,following))
                .thenReturn(Optional.empty());

        boolean result = followService.toggleFollow(1L,2L);

        assertTrue(result);

        verify(followRepository).save(any(Follow.class));
        verify(notificationService).createNotification(
                1L,2L,null,NotificationType.FOLLOW,null
        );
    }
}