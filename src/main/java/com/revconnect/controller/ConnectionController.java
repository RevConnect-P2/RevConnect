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
        List<Connection> pendingReceived = connectionService.getPendingRequests(currentUser);
        List<Connection> pendingSent = connectionService.getPendingSentRequests(currentUser);

        // FOLLOW DATA
        List<User> followers = followService.getFollowers(currentUser);
        List<User> following = followService.getFollowing(currentUser);

        // AVAILABLE USERS
        List<User> availableUsers = userService.getAllOtherUsers(currentUser.getUserId());

        model.addAttribute("followers", followers);
        model.addAttribute("following", following);
        model.addAttribute("availableUsers", availableUsers);

        model.addAttribute("connections", connections);
        model.addAttribute("pendingRequests", pendingReceived);
        model.addAttribute("user", currentUser);

        // CONNECTION STATUS
        Map<Long, String> connectionStatusMap = new HashMap<>();

        // FOLLOW STATUS
        Map<Long, Boolean> followStatusMap = new HashMap<>();

        for (User u : availableUsers) {

            if (connections.stream().anyMatch(c ->
                    (c.getSender().getUserId().equals(currentUser.getUserId()) &&
                            c.getReceiver().getUserId().equals(u.getUserId())) ||
                            (c.getReceiver().getUserId().equals(currentUser.getUserId()) &&
                                    c.getSender().getUserId().equals(u.getUserId()))
            )) {
                connectionStatusMap.put(u.getUserId(), "Connected");
            }

            else if (pendingSent.stream().anyMatch(req ->
                    req.getReceiver().getUserId().equals(u.getUserId())
            )) {
                connectionStatusMap.put(u.getUserId(), "Requested");
            }

            else {
                connectionStatusMap.put(u.getUserId(), "Connect");
            }

            boolean isFollowing = following.stream()
                    .anyMatch(f -> f.getUserId().equals(u.getUserId()));

            followStatusMap.put(u.getUserId(), isFollowing);
        }

        model.addAttribute("connectionStatusMap", connectionStatusMap);
        model.addAttribute("followStatusMap", followStatusMap);

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