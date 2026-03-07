package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.NotificationService;
import com.revconnect.service.PostService;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final PostService postService;
    private final NotificationService notificationService; // 🔔 add this

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        // ✅ Get logged-in user
        User user = (User) session.getAttribute("loggedUser");

        // ✅ Security check
        if (user == null) {
            return "redirect:/login";
        }

        // ✅ Send user to dashboard
        model.addAttribute("user", user);

        // 🔔 ADD NOTIFICATION COUNT
        long unreadCount =
                notificationService.getUnreadCount(user.getUserId());

        model.addAttribute("unreadCount", unreadCount);

        // ✅ GLOBAL FEED
        model.addAttribute(
                "posts",
                postService.getGlobalFeed(user.getUserId())
        );

        // ✅ TRENDING HASHTAGS
        model.addAttribute(
                "trendingTags",
                postService.getTrendingHashtags()
        );

        // ✅ Temporary stats
        model.addAttribute("connectionsCount", 0);
        model.addAttribute("followersCount", 0);
        model.addAttribute("followingCount", 0);

        // ✅ Optional welcome message
        model.addAttribute("message", "Welcome to RevConnect Dashboard");

        // ✅ Load dashboard page
        return "dashboard/dashboard";
    }
}