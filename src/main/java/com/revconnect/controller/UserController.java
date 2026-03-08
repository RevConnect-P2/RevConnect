package com.revconnect.controller;

import com.revconnect.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🔍 SEARCH USERNAMES API
    @GetMapping("/search")
    public List<String> searchUsers(@RequestParam String keyword) {

        return userService.searchUsernames(keyword);

    }
}