package com.revconnect.controller;

import com.revconnect.enums.ConnectionStatus;
import com.revconnect.service.ConnectionService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
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
    public String acceptConnection(@PathVariable Long connectionId) {

        connectionService.acceptRequest(connectionId);

        return "redirect:/network";
    }

    // =========================
    // REJECT CONNECTION REQUEST
    // =========================
    @PostMapping("/reject/{connectionId}")
    public String rejectConnection(@PathVariable Long connectionId) {

        connectionService.rejectRequest(connectionId);

        return "redirect:/network";
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

    // =========================
    // GET CONNECTION STATUS
    // =========================
    @GetMapping("/status/{user1}/{user2}")
    public String getConnectionStatus(
            @PathVariable Long user1,
            @PathVariable Long user2){

        ConnectionStatus status =
                connectionService.getConnectionStatus(user1, user2);

        if(status == null){
            return "NONE";
        }

        return status.name();
    }

}