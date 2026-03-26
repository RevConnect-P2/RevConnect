package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.FollowService;
import com.revconnect.service.NotificationService;
import com.revconnect.service.PostService;

import com.revconnect.entity.UserProfile;
import com.revconnect.repository.UserProfileRepository;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// ✅ LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    // LOGGER OBJECT
    private static final Logger logger =
            LogManager.getLogger(DashboardController.class);

    private final PostService postService;
    private final NotificationService notificationService;

    // NEW SERVICES
    private final FollowService followService;
    private final ConnectionService connectionService;

    // Profile pic update
    private final UserProfileRepository userProfileRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        logger.info("Dashboard requested");

        // Get logged-in user
        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {

            logger.warn("Dashboard access attempted without login");

            return "redirect:/login";
        }

        logger.info("Loading dashboard for user {}", user.getUserId());

        model.addAttribute("user", user);

        //new code added
        UserProfile userProfile = userProfileRepository
                .findByUser_UserId(user.getUserId())
                .orElse(null);

        model.addAttribute("userProfile", userProfile);

        //  Notification count
        long unreadCount =
                notificationService.getUnreadCount(user.getUserId());

        logger.info("Unread notifications for user {} = {}", user.getUserId(), unreadCount);

        model.addAttribute("unreadCount", unreadCount);

        //  Followers / Following
        long followers =
                followService.getFollowersCount(user.getUserId());

        long following =
                followService.getFollowingCount(user.getUserId());

        logger.info("Followers: {}, Following: {} for user {}", followers, following, user.getUserId());

        //  Connections
        long connections =
                connectionService.getConnectionsCount(user.getUserId());

        logger.info("Connections count for user {} = {}", user.getUserId(), connections);

        model.addAttribute("followersCount", followers);
        model.addAttribute("followingCount", following);
        model.addAttribute("connectionsCount", connections);

        // Feed
        model.addAttribute(
                "posts",
                postService.getGlobalFeed(user.getUserId())
        );

        logger.info("Global feed loaded for user {}", user.getUserId());

        // Trending hashtags
        model.addAttribute(
                "trendingTags",
                postService.getTrendingHashtags()
        );

        logger.info("Trending hashtags loaded");

        model.addAttribute("message", "Welcome to RevConnect Dashboard");

        return "dashboard/dashboard";
    }
}