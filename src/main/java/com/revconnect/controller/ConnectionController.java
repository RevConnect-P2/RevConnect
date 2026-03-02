package com.revconnect.controller;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.FollowService;
import com.revconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/network")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;
    private final UserService userService;
    private final FollowService followService;

    // MAIN NETWORK PAGE
    @GetMapping
    public String connectionsPage(Principal principal, Model model) {
        User currentUser = userService.getUserByEmailOrThrow(principal.getName());

        List<Connection> connections = connectionService.getConnections(currentUser);
        List<Connection> pendingReceived = connectionService.getPendingRequests(currentUser); // received requests
        List<Connection> pendingSent = connectionService.getPendingSentRequests(currentUser); // **you need this method**

        model.addAttribute("followers", followService.getFollowers(currentUser));
        model.addAttribute("following", followService.getFollowing(currentUser));
        model.addAttribute("availableUsers", userService.getAllOtherUsers(currentUser.getUserId()));

        model.addAttribute("connections", connections);
        model.addAttribute("pendingRequests", pendingReceived);
        model.addAttribute("user", currentUser);

        // Build userStatusMap
        Map<Long, String> userStatusMap = new HashMap<>();

        List<User> following = followService.getFollowing(currentUser);

        for (User u : userService.getAllOtherUsers(currentUser.getUserId())) {

            if (connections.stream().anyMatch(c ->
                    (c.getSender().getUserId().equals(currentUser.getUserId()) && c.getReceiver().getUserId().equals(u.getUserId())) ||
                            (c.getReceiver().getUserId().equals(currentUser.getUserId()) && c.getSender().getUserId().equals(u.getUserId()))
            )) {
                userStatusMap.put(u.getUserId(), "Connected");
            }

            else if (pendingSent.stream().anyMatch(req ->
                    req.getReceiver().getUserId().equals(u.getUserId())
            )) {
                userStatusMap.put(u.getUserId(), "Requested");
            }

            else if (following.stream().anyMatch(f ->
                    f.getUserId().equals(u.getUserId())
            )) {
                userStatusMap.put(u.getUserId(), "Following");
            }

            else {
                userStatusMap.put(u.getUserId(), "Connect/Follow");
            }
        }

        model.addAttribute("userStatusMap", userStatusMap);

        return "network/network";
    }
    // SEND CONNECTION REQUEST
    @PostMapping("/request/{userId}")
    public String sendRequest(@PathVariable Long userId, Principal principal) {
        User sender = userService.getUserByEmailOrThrow(principal.getName());
        User receiver = userService.getUserByIdOrThrow(userId);
        connectionService.sendRequest(sender, receiver);
        return "redirect:/network";
    }

    // ACCEPT CONNECTION
    @PostMapping("/accept/{connectionId}")
    public String acceptRequest(@PathVariable Long connectionId, Principal principal) {
        User currentUser = userService.getUserByEmailOrThrow(principal.getName());
        connectionService.acceptRequest(connectionId, currentUser);
        return "redirect:/network";
    }

    // REJECT CONNECTION
    @PostMapping("/reject/{connectionId}")
    public String rejectRequest(@PathVariable Long connectionId, Principal principal) {
        User currentUser = userService.getUserByEmailOrThrow(principal.getName());
        connectionService.rejectRequest(connectionId, currentUser);
        return "redirect:/network";
    }

    // REMOVE CONNECTION
    @PostMapping("/remove/{connectionId}")
    public String removeConnection(@PathVariable Long connectionId, Principal principal) {
        User currentUser = userService.getUserByEmailOrThrow(principal.getName());
        connectionService.removeConnection(connectionId, currentUser);
        return "redirect:/network";
    }
}