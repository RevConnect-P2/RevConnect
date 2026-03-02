package com.revconnect.mapper;

import com.revconnect.dto.response.PostResponse;
import com.revconnect.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostResponse toResponse(Post post) {

        if (post == null) {
            return null;
        }

        PostResponse response = new PostResponse();

        response.setPostId(post.getPostId());
        response.setContent(post.getContent());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setPostType(post.getPostType());
        response.setCtaLink(post.getCtaLink());
        response.setCtaText(post.getCtaText());
        response.setPinned(post.getPinned());
        response.setScheduledAt(post.getScheduledAt());
        response.setUserId(post.getUser().getUserId());

        return response;
    }
}