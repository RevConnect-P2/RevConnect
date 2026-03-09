package com.revconnect.controller;

import com.revconnect.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/connections")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;

    // =========================
    // SEND CONNECTION REQUEST
    // =========================
    @PostMapping("/request/{senderId}/{receiverId}")
    public String sendRequest(
            @PathVariable Long senderId,
            @PathVariable Long receiverId) {

        connectionService.sendConnectionRequest(senderId, receiverId);

        return "Connection request sent successfully";
    }

    // =========================
    // ACCEPT CONNECTION REQUEST
    // =========================
    @PostMapping("/accept/{connectionId}")
    public String acceptRequest(@PathVariable Long connectionId) {

        connectionService.acceptRequest(connectionId);

        return "Connection accepted";
    }

    // =========================
    // REJECT CONNECTION REQUEST
    // =========================
    @PostMapping("/reject/{connectionId}")
    public String rejectRequest(@PathVariable Long connectionId) {

        connectionService.rejectRequest(connectionId);

        return "Connection rejected";
    }

    // =========================
    // REMOVE CONNECTION
    // =========================
    @DeleteMapping("/remove/{connectionId}")
    public String removeConnection(@PathVariable Long connectionId) {

        connectionService.removeConnection(connectionId);

        return "Connection removed";
    }

    // =========================
    // COUNT USER CONNECTIONS
    // =========================
    @GetMapping("/count/{userId}")
    public long getConnectionsCount(@PathVariable Long userId) {

        return connectionService.getConnectionsCount(userId);
    }


}