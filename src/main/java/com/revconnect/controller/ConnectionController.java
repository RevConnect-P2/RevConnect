package com.revconnect.controller;

import com.revconnect.enums.ConnectionStatus;
import com.revconnect.service.ConnectionService;
import com.revconnect.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// ✅ LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
@RequestMapping("/connections")
@RequiredArgsConstructor
public class ConnectionController {

    // ✅ LOGGER OBJECT
    private static final Logger logger =
            LogManager.getLogger(ConnectionController.class);

    private final ConnectionService connectionService;

    @PostMapping("/request/{senderId}/{receiverId}")
    @ResponseBody
    public ResponseEntity<String> sendRequest(
            @PathVariable Long senderId,
            @PathVariable Long receiverId) {

        logger.info("Connection request from user {} to user {}", senderId, receiverId);

        connectionService.sendConnectionRequest(senderId, receiverId);

        logger.info("Connection request sent successfully from {} to {}", senderId, receiverId);

        return ResponseEntity.ok("Request Sent");
    }

    // =========================
    // ACCEPT CONNECTION REQUEST
    // =========================
    @PostMapping("/accept/{connectionId}")
    public String acceptConnection(@PathVariable Long connectionId) {

        logger.info("Accepting connection request with ID {}", connectionId);

        connectionService.acceptRequest(connectionId);

        logger.info("Connection accepted for ID {}", connectionId);

        return "redirect:/network";
    }

    // =========================
    // REJECT CONNECTION REQUEST
    // =========================
    @PostMapping("/reject/{connectionId}")
    public String rejectConnection(@PathVariable Long connectionId) {

        logger.info("Rejecting connection request with ID {}", connectionId);

        connectionService.rejectRequest(connectionId);

        logger.info("Connection rejected for ID {}", connectionId);

        return "redirect:/network";
    }

    // =========================
    // REMOVE CONNECTION
    // =========================
    @PostMapping("/remove/{connectionId}")
    public String removeConnection(@PathVariable Long connectionId) {

        logger.info("Removing connection with ID {}", connectionId);

        connectionService.removeConnection(connectionId);

        logger.info("Connection removed with ID {}", connectionId);

        return "redirect:/network";
    }

    // =========================
    // COUNT USER CONNECTIONS
    // =========================
    @GetMapping("/count/{userId}")
    public long getConnectionsCount(@PathVariable Long userId) {

        logger.info("Fetching connection count for user {}", userId);

        return connectionService.getConnectionsCount(userId);
    }

    // =========================
    // GET CONNECTION STATUS
    // =========================
    @GetMapping("/status/{user1}/{user2}")
    public String getConnectionStatus(
            @PathVariable Long user1,
            @PathVariable Long user2){

        logger.info("Checking connection status between {} and {}", user1, user2);

        ConnectionStatus status =
                connectionService.getConnectionStatus(user1, user2);

        if(status == null){
            logger.info("No connection exists between {} and {}", user1, user2);
            return "NONE";
        }

        logger.info("Connection status between {} and {} is {}", user1, user2, status.name());

        return status.name();
    }

    // =========================
    // GET MY CONNECTIONS (NO DUPLICATES)
    // =========================
    @GetMapping("/my/{userId}")
    public List<User> getMyConnections(@PathVariable Long userId) {

        logger.info("Fetching connections list for user {}", userId);

        return connectionService.getMyConnections(userId);
    }
}