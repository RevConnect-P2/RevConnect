package com.revconnect.mapper;

import com.revconnect.dto.response.PostResponse;
import com.revconnect.dto.response.TagResponse;
import com.revconnect.entity.Hashtag;
import com.revconnect.entity.Post;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {

    public PostResponse toPostResponse(
            Post post,
            List<Hashtag> hashtags,
            List<TagResponse> tags
    ) {

        return PostResponse.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .postType(post.getPostType())
                .pinned(post.getPinned())
                .ctaText(post.getCtaText())
                .ctaLink(post.getCtaLink())
                .scheduledAt(post.getScheduledAt())
                .createdAt(post.getCreatedAt())
                .userId(post.getUser().getUserId())
                .username(post.getUser().getUsername())

                // ✅ Hashtags (unchanged behavior)
                .hashtags(
                        hashtags == null
                                ? List.of()
                                : hashtags.stream()
                                .map(Hashtag::getTagName)
                                .collect(Collectors.toList())
                )

                // ✅ Product / Service Tags
                .tags(tags == null ? List.of() : tags)

                .build();
    }
}