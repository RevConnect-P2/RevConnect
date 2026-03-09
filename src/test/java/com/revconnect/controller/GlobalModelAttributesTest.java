package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import com.revconnect.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GlobalModelAttributesTest {

    private GlobalModelAttributes globalModelAttributes;

    private UserRepository userRepository;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {

        userRepository = mock(UserRepository.class);
        notificationService = mock(NotificationService.class);

        globalModelAttributes =
                new GlobalModelAttributes(userRepository, notificationService);
    }

    // ===== PRINCIPAL NULL =====

    @Test
    void unreadCount_shouldReturnZero_whenPrincipalNull() {

        long result = globalModelAttributes.unreadCount(null);

        assertEquals(0, result);
    }

    // ===== USER NOT FOUND =====

    @Test
    void unreadCount_shouldReturnZero_whenUserNotFound() {

        Principal principal = () -> "test@mail.com";

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        long result = globalModelAttributes.unreadCount(principal);

        assertEquals(0, result);
    }

    // ===== USER FOUND =====

    @Test
    void unreadCount_shouldReturnNotificationCount_whenUserExists() {

        Principal principal = () -> "test@mail.com";

        User user = new User();
        user.setUserId(1L);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(notificationService.getUnreadCount(1L))
                .thenReturn(5L);

        long result = globalModelAttributes.unreadCount(principal);

        assertEquals(5L, result);

        verify(notificationService).getUnreadCount(1L);
    }
}