package com.revconnect.controller;

import com.revconnect.repository.UserRepository;
import com.revconnect.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @ModelAttribute("unreadCount")
    public long unreadCount(Principal principal) {

        if (principal == null) {
            return 0L;
        }

        String email = principal.getName();

        return userRepository.findByEmail(email)
                .map(user -> notificationService.getUnreadCount(user.getUserId()))
                .orElse(0L);
    }
}