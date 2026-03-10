package com.revconnect.controller;

import com.revconnect.dto.response.NotificationResponse;
import com.revconnect.entity.User;
import com.revconnect.enums.NotificationType;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.NotificationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private NotificationController controller;

    // =========================
    // VIEW NOTIFICATIONS
    // =========================
    @Test
    void shouldViewNotifications() {

        User user = new User();
        user.setUserId(1L);

        NotificationResponse response = new NotificationResponse();

        when(principal.getName()).thenReturn("test@mail.com");
        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(notificationService.getUserNotifications(1L))
                .thenReturn(List.of(response));

        String view = controller.viewNotifications(model, principal);

        assertEquals("notifications/notifications", view);

        verify(model).addAttribute(eq("notifications"), any());
    }

    // =========================
    // FILTER NOTIFICATIONS
    // =========================
    @Test
    void shouldFilterNotifications() {

        User user = new User();
        user.setUserId(1L);

        NotificationResponse response = new NotificationResponse();

        when(principal.getName()).thenReturn("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(notificationService.getNotificationsByType(1L, NotificationType.LIKE))
                .thenReturn(List.of(response));

        when(notificationService.getUnreadCountByType(1L, NotificationType.LIKE))
                .thenReturn(5L);

        String view = controller.filterNotifications(
                NotificationType.LIKE,
                model,
                principal
        );

        assertEquals("notifications/notifications", view);
    }

    // =========================
    // MARK READ
    // =========================
    @Test
    void shouldMarkNotificationRead() {

        String view = controller.markRead(10L, null);

        assertEquals("redirect:/notifications", view);

        verify(notificationService).markAsRead(10L);
    }

    // =========================
    // MARK UNREAD
    // =========================
    @Test
    void shouldMarkNotificationUnread() {

        String view = controller.markUnread(5L, null);

        assertEquals("redirect:/notifications", view);

        verify(notificationService).markAsUnread(5L);
    }

    // =========================
    // DELETE NOTIFICATION
    // =========================
    @Test
    void shouldDeleteNotification() {

        String view = controller.deleteNotification(3L);

        assertEquals("redirect:/notifications", view);

        verify(notificationService).deleteNotification(3L);
    }

    // =========================
    // MARK ALL READ
    // =========================
    @Test
    void shouldMarkAllRead() {

        User user = new User();
        user.setUserId(1L);

        when(principal.getName()).thenReturn("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        String view = controller.markAllRead(principal);

        assertEquals("redirect:/notifications", view);

        verify(notificationService).markAllAsRead(1L);
    }

    // =========================
// NOTIFICATION COUNT (principal null)
// =========================
    @Test
    void notificationCount_shouldSetZero_whenPrincipalNull() {

        controller.notificationCount(model, null);

        verify(model).addAttribute("unreadCount", 0);
    }

    // =========================
// NOTIFICATION COUNT (principal present)
// =========================
    @Test
    void notificationCount_shouldReturnUnreadCount() {

        User user = new User();
        user.setUserId(1L);

        when(principal.getName()).thenReturn("test@mail.com");
        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(notificationService.getUnreadCount(1L))
                .thenReturn(3L);

        controller.notificationCount(model, principal);

        verify(model).addAttribute("unreadCount", 3L);
    }

    // =========================
// FILTER WITHOUT TYPE
// =========================
    @Test
    void shouldFilterNotifications_whenTypeNull() {

        User user = new User();
        user.setUserId(1L);

        when(principal.getName()).thenReturn("test@mail.com");
        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(notificationService.getUserNotifications(1L))
                .thenReturn(List.of());

        when(notificationService.getUnreadCount(1L))
                .thenReturn(2L);

        String view = controller.filterNotifications(null, model, principal);

        assertEquals("notifications/notifications", view);

        verify(model).addAttribute(eq("unreadCount"), eq(2L));
    }

    // =========================
// MARK READ WITH TYPE
// =========================
    @Test
    void shouldRedirectFilteredPage_whenMarkReadWithType() {

        String view = controller.markRead(5L, NotificationType.LIKE);

        assertEquals("redirect:/notifications/filter?type=LIKE", view);

        verify(notificationService).markAsRead(5L);
    }

    // =========================
// MARK UNREAD WITH TYPE
// =========================
    @Test
    void shouldRedirectFilteredPage_whenMarkUnreadWithType() {

        String view = controller.markUnread(6L, NotificationType.COMMENT);

        assertEquals("redirect:/notifications/filter?type=COMMENT", view);

        verify(notificationService).markAsUnread(6L);
    }


}