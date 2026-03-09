package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.entity.UserProfile;
import com.revconnect.repository.UserRepository;
import com.revconnect.repository.UserProfileRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfilePageControllerTest {

    private ProfilePageController controller;

    private UserRepository userRepository;
    private UserProfileRepository userProfileRepository;

    private Model model;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userProfileRepository = mock(UserProfileRepository.class);
        controller = new ProfilePageController(userRepository, userProfileRepository);

        model = mock(Model.class);
    }

    // ================= USER FOUND =================

    @Test
    void viewProfile_shouldLoadProfile_whenUserExists() {

        User user = new User();
        user.setUserId(1L);
        user.setUsername("john");

        UserProfile profile = new UserProfile();

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userProfileRepository.findByUser_UserId(1L))
                .thenReturn(Optional.of(profile));

        String view = controller.viewProfile("john", model);

        assertEquals("profile/public-profile", view);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("profile", profile);
    }

    // ================= USER NOT FOUND =================

    @Test
    void viewProfile_shouldThrowException_whenUserNotFound() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> controller.viewProfile("unknown", model)
        );

        assertEquals("User not found", exception.getMessage());
    }
}