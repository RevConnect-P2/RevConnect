package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.NotificationService;
import com.revconnect.service.PostService;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final NotificationService notificationService;

    // ✅ Constructor Injection
    public DashboardController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    private final PostService postService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        // ✅ Get logged-in user from session
        User user = (User) session.getAttribute("loggedUser");

        // ✅ Security check
        if (user == null) {
            return "redirect:/login";
        }

        // ✅ Add user to model
        model.addAttribute("user", user);

        // ✅ Add unread notification count
        long unreadCount =
                notificationService.getUnreadCount(user.getUserId());

        model.addAttribute("unreadCount", unreadCount);

        // ✅ Temporary empty post list
        model.addAttribute("posts", new ArrayList<>());
        // ✅ Send user to dashboard
        model.addAttribute("user", user);

        // ✅ FIXED GLOBAL FEED (visibility-aware)
        model.addAttribute(
                "posts",
                postService.getGlobalFeed(user.getUserId())
        );

        // ✅ Temporary stats
        model.addAttribute("connectionsCount", 0);
        model.addAttribute("followersCount", 0);
        model.addAttribute("followingCount", 0);

        model.addAttribute("message",
                "Welcome to RevConnect Dashboard");

        // ✅ Optional welcome message
        model.addAttribute("message", "Welcome to RevConnect Dashboard");

        // ✅ Load dashboard page
        return "dashboard/dashboard";
    }
}