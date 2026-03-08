package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.FollowService;
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
    private final NotificationService notificationService;

    // ⭐ NEW SERVICES
    private final FollowService followService;
    private final ConnectionService connectionService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        // ✅ Get logged-in user
        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        // 🔔 Notification count
        long unreadCount =
                notificationService.getUnreadCount(user.getUserId());

        model.addAttribute("unreadCount", unreadCount);

        // 📊 Followers / Following
        long followers =
                followService.getFollowersCount(user.getUserId());

        long following =
                followService.getFollowingCount(user.getUserId());

        // 🤝 Connections
        long connections =
                connectionService.getConnectionsCount(user.getUserId());

        model.addAttribute("followersCount", followers);
        model.addAttribute("followingCount", following);
        model.addAttribute("connectionsCount", connections);

        // 📰 Feed
        model.addAttribute(
                "posts",
                postService.getGlobalFeed(user.getUserId())
        );

        // 🔥 Trending hashtags
        model.addAttribute(
                "trendingTags",
                postService.getTrendingHashtags()
        );

        model.addAttribute("message", "Welcome to RevConnect Dashboard");

        return "dashboard/dashboard";
    }
}