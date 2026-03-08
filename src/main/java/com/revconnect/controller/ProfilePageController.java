package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.entity.UserProfile;
import com.revconnect.repository.UserProfileRepository;
import com.revconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ProfilePageController {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    // ===============================
    // MY PROFILE (LOGGED-IN USER)
//    // ===============================
//    @GetMapping("/profile")
//    public String myProfile(Authentication authentication, Model model) {
//
//        String username = authentication.getName();
//
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        UserProfile profile = userProfileRepository
//                .findByUser_UserId(user.getUserId())
//                .orElse(null);
//
//        model.addAttribute("user", user);
//        model.addAttribute("profile", profile);
//
//        return "profile/profile";
//    }

    // ===============================
    // PUBLIC PROFILE
    // ===============================
    @GetMapping("/profile/{username}")
    public String viewProfile(@PathVariable String username, Model model) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository
                .findByUser_UserId(user.getUserId())
                .orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);

        return "profile/public-profile";
    }
}