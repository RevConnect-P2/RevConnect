package com.revconnect.service.impl;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.response.PostResponse;
import com.revconnect.entity.*;
import com.revconnect.exception.BadRequestException;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.mapper.PostMapper;
import com.revconnect.repository.*;
import com.revconnect.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final PostMapper postMapper;

    @Override
    public PostResponse createPost(Long userId, PostCreateRequest request) {

        // 1️⃣ Validate content
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BadRequestException("Post content cannot be empty");
        }

        // 2️⃣ Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3️⃣ Validate post type (string-based for now)
        String postType = request.getPostType();
        if (postType == null ||
                (!postType.equalsIgnoreCase("NORMAL") &&
                        !postType.equalsIgnoreCase("PROMOTIONAL"))) {
            throw new BadRequestException("Invalid post type");
        }

        // 4️⃣ Validate CTA for promotional posts
        if (postType.equalsIgnoreCase("PROMOTIONAL")) {
            if (request.getCtaText() == null || request.getCtaLink() == null) {
                throw new BadRequestException("CTA text and link are required for promotional posts");
            }
        }

        // 5️⃣ Create Post entity
        Post post = Post.builder()
                .user(user)
                .content(request.getContent())
                .postType(postType.toUpperCase())
                .pinned(request.getPinned() != null ? request.getPinned() : false)
                .ctaText(request.getCtaText())
                .ctaLink(request.getCtaLink())
                .scheduledAt(request.getScheduledAt())
                .build();

        Post savedPost = postRepository.save(post);

        // 6️⃣ Handle hashtags
        List<String> hashtagNames = request.getHashtags();
        List<Hashtag> hashtags = new ArrayList<>();

        if (hashtagNames != null && !hashtagNames.isEmpty()) {

            for (String tag : hashtagNames) {
                if (tag == null || tag.trim().isEmpty()) continue;

                String normalizedTag = tag.trim().toLowerCase();

                Hashtag hashtag = hashtagRepository
                        .findByTagName(normalizedTag)
                        .orElseGet(() -> hashtagRepository.save(
                                Hashtag.builder()
                                        .tagName(normalizedTag)
                                        .build()
                        ));

                PostHashtag postHashtag = PostHashtag.builder()
                        .post(savedPost)
                        .hashtag(hashtag)
                        .build();

                postHashtagRepository.save(postHashtag);
                hashtags.add(hashtag);
            }
        }

        // 7️⃣ Map to response
        return postMapper.toPostResponse(savedPost, hashtags);
    }

    // Other methods will be implemented next
}