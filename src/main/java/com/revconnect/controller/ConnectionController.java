package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/connections")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;
    private final UserService userService;

    @PostMapping("/request/{receiverId}")
    public String sendRequest(@PathVariable Long receiverId,
                              Authentication authentication) {

        // Logged in user email
        String email = authentication.getName();

        User sender = userService.findByEmail(email);
        User receiver = userService.findById(receiverId);

        connectionService.sendConnectionRequest(sender, receiver);

        return "redirect:/dashboard";
    }
}