package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.repository.ConnectionRepository;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class NetworkPageController {

    private final ConnectionRepository connectionRepository;

    @GetMapping("/network")
    public String networkPage(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/login";
        }

        var connections = connectionRepository
                .findBySender_UserId(user.getUserId());

        model.addAttribute("connections", connections);

        return "connection/network";
    }
}