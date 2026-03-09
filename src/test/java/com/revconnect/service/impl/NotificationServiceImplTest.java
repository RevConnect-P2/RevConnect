package com.revconnect.service.impl;

import com.revconnect.dto.response.NotificationResponse;
import com.revconnect.entity.Notification;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.mapper.NotificationMapper;
import com.revconnect.repository.NotificationRepository;
import com.revconnect.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User sender;
    private User receiver;
    private Notification notification;

    @Before
    public void setup() {

        sender = new User();
        sender.setUserId(1L);
        sender.setUsername("john");

        receiver = new User();
        receiver.setUserId(2L);

        notification = new Notification();
        notification.setReceiver(receiver);
    }

    // ---------------- CREATE NOTIFICATION ----------------

    @Test
    public void shouldCreateNotificationSuccessfully() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        notificationService.createNotification(
                1L,
                2L,
                100L,
                NotificationType.LIKE,
                null
        );

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    public void shouldNotCreateSelfNotification() {

        notificationService.createNotification(
                1L,
                1L,
                100L,
                NotificationType.LIKE,
                null
        );

        verify(notificationRepository, never()).save(any());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfSenderNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        notificationService.createNotification(
                1L,
                2L,
                100L,
                NotificationType.LIKE,
                null
        );
    }

    // ---------------- GET USER NOTIFICATIONS ----------------

    @Test
    public void shouldReturnUserNotifications() {

        NotificationResponse response = new NotificationResponse();

        when(notificationRepository
                .findByReceiver_UserIdOrderByCreatedAtDesc(2L))
                .thenReturn(List.of(notification));

        when(notificationMapper.toResponse(notification))
                .thenReturn(response);

        List<NotificationResponse> result =
                notificationService.getUserNotifications(2L);

        assertEquals(1, result.size());
    }

    // ---------------- UNREAD COUNT ----------------

    @Test
    public void shouldReturnUnreadCount() {

        when(notificationRepository
                .countByReceiver_UserIdAndReadFalse(2L))
                .thenReturn(5L);

        long count = notificationService.getUnreadCount(2L);

        assertEquals(5L, count);
    }

    // ---------------- MARK AS READ ----------------

    @Test
    public void shouldMarkAsRead() {

        when(notificationRepository.findById(10L))
                .thenReturn(Optional.of(notification));

        notificationService.markAsRead(10L);

        verify(notificationRepository).save(notification);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfNotificationNotFound() {

        when(notificationRepository.findById(10L))
                .thenReturn(Optional.empty());

        notificationService.markAsRead(10L);
    }

    // ---------------- MARK AS UNREAD ----------------

    @Test
    public void shouldMarkAsUnread() {

        when(notificationRepository.findById(10L))
                .thenReturn(Optional.of(notification));

        notificationService.markAsUnread(10L);

        verify(notificationRepository).save(notification);
    }

    // ---------------- MARK ALL AS READ ----------------

    @Test
    public void shouldMarkAllAsRead() {

        when(notificationRepository
                .findByReceiver_UserIdAndReadFalseOrderByCreatedAtDesc(2L))
                .thenReturn(List.of(notification));

        notificationService.markAllAsRead(2L);

        verify(notificationRepository).saveAll(anyList());
    }

    // ---------------- DELETE ----------------

    @Test
    public void shouldDeleteNotification() {

        notificationService.deleteNotification(10L);

        verify(notificationRepository).deleteById(10L);
    }

    // ---------------- FILTER BY TYPE ----------------

    @Test
    public void shouldReturnNotificationsByType() {

        NotificationResponse response = new NotificationResponse();

        when(notificationRepository
                .findByReceiver_UserIdAndTypeOrderByCreatedAtDesc(
                        2L, NotificationType.LIKE))
                .thenReturn(List.of(notification));

        when(notificationMapper.toResponse(notification))
                .thenReturn(response);

        List<NotificationResponse> result =
                notificationService.getNotificationsByType(
                        2L,
                        NotificationType.LIKE
                );

        assertEquals(1, result.size());
    }

    // ---------------- UNREAD BY TYPE ----------------

    @Test
    public void shouldReturnUnreadCountByType() {

        when(notificationRepository
                .countByReceiver_UserIdAndTypeAndReadFalse(
                        2L,
                        NotificationType.LIKE))
                .thenReturn(3L);

        long count =
                notificationService.getUnreadCountByType(
                        2L,
                        NotificationType.LIKE
                );

        assertEquals(3L, count);
    }
}