package com.revconnect.controller;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import com.revconnect.enums.ConnectionStatus;
import com.revconnect.repository.ConnectionRepository;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NetworkPageController {

    private final ConnectionRepository connectionRepository;

    // =========================
    // MY NETWORK PAGE
    // =========================
    @GetMapping("/network")
    public String networkPage(HttpSession session, Model model) {

        // Get logged-in user from session
        User loggedUser = (User) session.getAttribute("loggedUser");

        // If session expired redirect to login
        if (loggedUser == null) {
            return "redirect:/login";
        }

        Long userId = loggedUser.getUserId();

        // =========================
        // ACCEPTED CONNECTIONS
        // =========================
        List<Connection> connections =
                connectionRepository
                        .findBySender_UserIdOrReceiver_UserIdAndStatus(
                                userId,
                                userId,
                                ConnectionStatus.ACCEPTED
                        );

        // =========================
        // PENDING REQUESTS
        // =========================
        List<Connection> pendingRequests =
                connectionRepository
                        .findByReceiver_UserIdAndStatus(
                                userId,
                                ConnectionStatus.PENDING
                        );

        // Send data to Thymeleaf page
        model.addAttribute("connections", connections);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("loggedUser", loggedUser);

        return "connection/network";
    }
}