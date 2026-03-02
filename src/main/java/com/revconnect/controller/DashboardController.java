package com.revconnect.controller;

import com.revconnect.entity.User;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.revconnect.service.PostService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList; // ✅ IMPORTANT

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final PostService postService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model)
    {

        // ✅ Get logged-in user
        User user = (User) session.getAttribute("loggedUser");


        // ✅ Security check
        if (user == null)
        {
            return "redirect:/login";
        }


        // ✅ Send user to dashboard
        model.addAttribute("user", user);


        // ✅ REAL POSTS NOW
        model.addAttribute(
                "posts",
                postService.getPostsByUser(user.getUserId())
        );


        // ✅ Temporary stats (team members will implement later)
        model.addAttribute("connectionsCount", 0);

        model.addAttribute("followersCount", 0);

        model.addAttribute("followingCount", 0);


        // ✅ Optional welcome message
        model.addAttribute("message", "Welcome to RevConnect Dashboard");


        // ✅ Load dashboard page
        return "dashboard/dashboard";

    }

}