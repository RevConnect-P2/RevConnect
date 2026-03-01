package com.revconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    // =========================
    // DASHBOARD
    // =========================
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // =========================
    // CREATE POST PAGE
    // =========================
    @GetMapping("/posts/create")
    public String createPostPage() {
        return "posts/create-post";
    }

    // =========================
    // MY POSTS / FEED PAGE
    // =========================
    @GetMapping("/posts/my")
    public String myPostsPage() {
        return "posts/post-feed";
    }
}