package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.FollowService;
import com.revconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/network/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    // FOLLOW USER
    @PostMapping("/follow/{userId}")
    public String follow(@PathVariable Long userId, Principal principal) {
        User follower = userService.getUserByEmailOrThrow(principal.getName()); // use only one method
        User following = userService.getUserByIdOrThrow(userId);
        followService.follow(follower, following);
        return "redirect:/network"; // redirect to main network page
    }

    // UNFOLLOW USER
    @PostMapping("/unfollow/{userId}")
    public String unfollow(@PathVariable Long userId, Principal principal) {
        User follower = userService.getUserByEmailOrThrow(principal.getName()); // consistent
        User following = userService.getUserByIdOrThrow(userId);
        followService.unfollow(follower, following);
        return "redirect:/network"; // redirect to main network page
    }
}