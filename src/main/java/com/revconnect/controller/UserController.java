package com.revconnect.controller;

import com.revconnect.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ✅ LOGGER IMPORTS
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/users")
public class UserController {

    // ✅ LOGGER OBJECT
    private static final Logger logger =
            LogManager.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🔍 SEARCH USERNAMES API
    @GetMapping("/search")
    public List<String> searchUsers(@RequestParam String keyword) {

        logger.info("User search API called with keyword: {}", keyword);

        List<String> users = userService.searchUsernames(keyword);

        logger.info("Found {} users for keyword: {}", users.size(), keyword);

        return users;

    }
}