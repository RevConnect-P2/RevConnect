package com.revconnect.controller;

import com.revconnect.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPostsPageController {

    @GetMapping("/posts")
    public String myPostsPage(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        // temporary stats
        model.addAttribute("connectionsCount", 0);
        model.addAttribute("followersCount", 0);
        model.addAttribute("followingCount", 0);

        return "posts/my-posts";
    }
}