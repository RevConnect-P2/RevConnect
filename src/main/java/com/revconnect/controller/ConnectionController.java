package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.FollowService;
import com.revconnect.service.UserService; // make sure you have this
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;
    private final FollowService followService;
    private final UserService userService; // add this

    @GetMapping("/network")
    public String loadNetworkPage(Model model) {

        Long userId = 1L; // temporary (later replace with logged user)

        // Fetch user
        User user = userService.getUserById(userId); // fetch the user from DB
        if (user == null) {
            user = new User(); // fallback to empty user object
        }

        model.addAttribute("user", user);

        model.addAttribute("connections",
                connectionService.getUserConnections(userId));

        model.addAttribute("pendingRequests",
                connectionService.getPendingRequests(userId));

        model.addAttribute("followers",
                followService.getFollowers(userId));

        model.addAttribute("following",
                followService.getFollowing(userId));

        model.addAttribute("connectionsCount",
                connectionService.getUserConnections(userId).size());

        model.addAttribute("followersCount",
                followService.getFollowers(userId).size());

        model.addAttribute("followingCount",
                followService.getFollowing(userId).size());

        return "network/network";   // loads network.html
    }

}