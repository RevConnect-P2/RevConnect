package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.service.ConnectionService;
import com.revconnect.service.FollowService;
import com.revconnect.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPostsPageController {

    private final FollowService followService;
    private final ConnectionService connectionService;
    private final PostService postService;

    public MyPostsPageController(FollowService followService, ConnectionService connectionService, PostService postService) {
        this.followService = followService;
        this.connectionService = connectionService;
        this.postService = postService;
    }

    @GetMapping("/posts")
    public String myPostsPage(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        long followers =
                followService.getFollowersCount(user.getUserId());

        long following =
                followService.getFollowingCount(user.getUserId());

        long connections =
                connectionService.getConnectionsCount(user.getUserId());

        model.addAttribute("followersCount", followers);
        model.addAttribute("followingCount", following);
        model.addAttribute("connectionsCount", connections);

        model.addAttribute(
                "trendingTags",
                postService.getTrendingHashtags()
        );
        return "posts/my-posts";
    }
}