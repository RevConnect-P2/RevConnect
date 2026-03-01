package com.revconnect.controller;

import com.revconnect.dto.response.ProfileResponse;
import com.revconnect.service.ProfileService;
import com.revconnect.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

        // 1️⃣ Get logged-in username
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // 2️⃣ Convert username → userId
        Long userId = userService.getUserIdByUsername(username);

        // 3️⃣ Fetch profile using EXISTING Member-2 method
        ProfileResponse profile = profileService.getProfile(userId);

        // 4️⃣ Send to UI
        model.addAttribute("profile", profile);

        return "profile";
    }
}