
package com.revconnect.controller;
import com.revconnect.common.ApiResponse;
import com.revconnect.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ApiResponse<?> getFeed(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String postType,
            @RequestParam(required = false) String userType) {

        return ApiResponse.success(
                feedService.getPersonalizedFeed(userId, page, size, postType, userType)
        );
    }

    @GetMapping("/trending")
    public ApiResponse<?> trending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.success(
                feedService.getTrendingPosts(page, size)
        );
    }

    @GetMapping("/hashtag")
    public ApiResponse<?> searchByHashtag(
            @RequestParam String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.success(
                feedService.searchByHashtag(tag, page, size)
        );
    }
}