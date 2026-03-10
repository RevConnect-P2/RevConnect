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

                // USER DETAILS
                .userId(post.getUser() != null ? post.getUser().getUserId() : null)
                .username(post.getUser() != null ? post.getUser().getUsername() : null)

                // HASHTAGS
                .hashtags(
                        hashtags == null
                                ? List.of()
                                : hashtags.stream()
                                .map(Hashtag::getTagName)
                                .collect(Collectors.toList())
                )

                // PRODUCT / SERVICE TAGS
                .tags(tags == null ? List.of() : tags)

                // DEFAULT COUNTS (service updates them)
                .likeCount(post.getLikes() != null ? (long) post.getLikes().size() : 0L)
                .commentCount(post.getComments() != null ? (long) post.getComments().size() : 0L)
                .shareCount(post.getShares() != null ? (long) post.getShares().size() : 0L)

                // SHARED POST INFO (default false)
                .isSharedPost(false)
                .sharedByUsername(null)
                .originalAuthorUsername(null)

                .build();
    }
}