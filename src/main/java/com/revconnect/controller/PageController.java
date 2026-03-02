package com.revconnect.controller;

import com.revconnect.dto.request.ProfileUpdateRequest; // ✅ ADD THIS
import com.revconnect.dto.response.ProfileResponse;
import com.revconnect.service.ProfileService;
import com.revconnect.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PageController {

    private final ProfileService profileService;
    private final UserService userService;

    public PageController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Long userId = userService.getUserIdByUsername(username);

        ProfileResponse profile = profileService.getProfile(userId);
        model.addAttribute("profile", profile);

        return "profile/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfilePage(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        ProfileResponse profile = profileService.getProfile(
                userService.getUserIdByUsername(username)
        );

        model.addAttribute("profile", profile);
        return "profile/edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(ProfileUpdateRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Long userId = userService.getUserIdByUsername(username);

        profileService.updateProfile(userId, request);

        return "redirect:/profile";
    }
}